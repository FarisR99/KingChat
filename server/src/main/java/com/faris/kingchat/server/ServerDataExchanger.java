package com.faris.kingchat.server;

import com.faris.kingchat.core.Constants;
import com.faris.kingchat.core.DataExchanger;
import com.faris.kingchat.core.packets.Packet;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class ServerDataExchanger extends DataExchanger {

	private final Server server;

	private Thread receiveThread = null;
	private ExecutorService sendExecutorService;

	public ServerDataExchanger(Server server) throws SocketException {
		super(new DatagramSocket(server.getPort()));
		this.server = server;
		this.sendExecutorService = Executors.newFixedThreadPool(10, new ThreadFactory() {
			private Set<Integer> ids = new HashSet<>();

			@Override
			public Thread newThread(Runnable r) {
				int id = this.generateId();
				if (id == -1) {
					return new Thread(r);
				} else {
					return new Thread(() -> {
						try {
							r.run();
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							this.ids.remove(id);
						}
					}, "SendService" + id);
				}
			}

			private int generateId() {
				for (int i = 0; i < Integer.MAX_VALUE; i++) {
					if (!this.ids.contains(i)) {
						this.ids.add(i);
						return i;
					}
				}
				return -1;
			}
		});
	}

	void receive() {
		this.receiveThread = new Thread(() -> {
			while (this.server.isRunning()) {
				byte[] data = new byte[Constants.DATA_SIZE];
				DatagramPacket packet = new DatagramPacket(data, data.length);
				try {
					this.socket.receive(packet);
				} catch (SocketException ignored) {
					break;
				} catch (IOException ex) {
					ex.printStackTrace();
					this.server.setRunning(false);
					break;
				}
				this.server.processPacketReceived(packet);
			}
		}, "Receive");
		this.receiveThread.start();
	}

	public void sendPacket(Packet packet, InetAddress address, int port) {
		this.sendPacket(packet, address, port, null, null);
	}

	public void sendPacket(Packet packet, InetAddress address, int port, Runnable onSuccess, Consumer<Throwable> failedConsumer) {
		JsonObject jsonPacket = packet.toJson();
		jsonPacket.addProperty("t", packet.getId());
		this.sendData(jsonPacket.toString(), address, port, onSuccess, failedConsumer);
	}

	public void sendPacket(Packet packet, Collection<Client> clients) {
		this.sendPacket(packet, clients, null);
	}

	public void sendPacket(Packet packet, Collection<Client> clients, Runnable onCompletion) {
		this.sendPacket(packet, clients, onCompletion, null, null);
	}

	public void sendPacket(Packet packet, Collection<Client> clients, Consumer<Client> successConsumer, BiConsumer<Client, Throwable> failedConsumer) {
		this.sendPacket(packet, clients, null, successConsumer, failedConsumer);
	}

	public void sendPacket(Packet packet, Collection<Client> clients, Runnable onCompletion, Consumer<Client> successConsumer, BiConsumer<Client, Throwable> failedConsumer) {
		JsonObject jsonPacket = packet.toJson();
		jsonPacket.addProperty("t", packet.getId());
		this.sendData(jsonPacket.toString(), clients, onCompletion, successConsumer, failedConsumer);
	}

	public void sendData(String data, InetAddress address, int port, Runnable onSuccess, Consumer<Throwable> failedConsumer) {
		if (data.length() == 0) throw new IllegalArgumentException("cannot send empty packet data");
		String message = Base64.getEncoder().encodeToString(data.getBytes());
		this.sendData((message + "/e").getBytes(), address, port, onSuccess, failedConsumer);
	}

	public void sendData(String data, Collection<Client> clients, Consumer<Client> successConsumer, BiConsumer<Client, Throwable> failedConsumer) {
		this.sendData(data, clients, null, successConsumer, failedConsumer);
	}

	public void sendData(String data, Collection<Client> clients, Runnable onCompletion, Consumer<Client> successConsumer, BiConsumer<Client, Throwable> failedConsumer) {
		if (data.length() == 0) throw new IllegalArgumentException("cannot send empty packet data");
		String message = Base64.getEncoder().encodeToString(data.getBytes());
		this.sendData((message + "/e").getBytes(), clients, onCompletion, successConsumer, failedConsumer);
	}

	private void sendData(final byte[] data, InetAddress address, int port, Runnable onSuccess, Consumer<Throwable> failedConsumer) {
		if (data.length > Constants.DATA_SIZE) {
			throw new IllegalArgumentException("data length cannot exceed " + Constants.DATA_SIZE + " bytes");
		}
		this.sendExecutorService.execute(() -> {
			DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
			try {
				this.socket.send(packet);
			} catch (Exception ex) {
				if (failedConsumer != null) failedConsumer.accept(ex);
				return;
			}
			if (onSuccess != null) onSuccess.run();
		});
	}

	private void sendData(final byte[] data, Collection<Client> clients, Runnable onCompletion, Consumer<Client> successConsumer, BiConsumer<Client, Throwable> failedConsumer) {
		if (data.length > Constants.DATA_SIZE) {
			throw new IllegalArgumentException("data length cannot exceed " + Constants.DATA_SIZE + " bytes");
		}
		final List<Client> receivers = new ArrayList<>(clients);
		if (receivers.isEmpty()) {
			if (onCompletion != null) onCompletion.run();
			return;
		}
		this.sendExecutorService.execute(() -> {
			for (Client receiver : receivers) {
				DatagramPacket packet = new DatagramPacket(data, data.length, receiver.getAddress(), receiver.getPort());
				try {
					this.socket.send(packet);
				} catch (Exception ex) {
					if (failedConsumer != null) failedConsumer.accept(receiver, ex);
					continue;
				}
				if (successConsumer != null) successConsumer.accept(receiver);
			}
			if (onCompletion != null) onCompletion.run();
		});
	}

}
