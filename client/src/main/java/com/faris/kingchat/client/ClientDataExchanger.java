package com.faris.kingchat.client;

import com.faris.kingchat.core.Constants;
import com.faris.kingchat.core.DataExchanger;
import com.faris.kingchat.core.packets.Packet;
import com.google.gson.JsonObject;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.logging.*;

public class ClientDataExchanger extends DataExchanger {

	private final Logger logger;
	private final InetAddress ipAddress;
	private final String addressName;
	private final int port;
	private UUID uuid = null;

	private ExecutorService sendExecutorService;
	private int timeout = 0;

	public ClientDataExchanger(Logger logger, String ipAddress, int port) throws SocketException, UnknownHostException {
		super(new DatagramSocket());
		this.logger = logger;
		this.addressName = ipAddress;
		this.ipAddress = InetAddress.getByName(ipAddress);
		this.port = port;

		this.sendExecutorService = Executors.newFixedThreadPool(5, new ThreadFactory() {
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

	public InetAddress getAddress() {
		return this.ipAddress;
	}

	public String getAddressName() {
		return this.addressName;
	}

	public int getPort() {
		return this.port;
	}

	public int getReadTimeout() {
		return this.timeout;
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public String receiveData() throws Exception {
		byte[] data = new byte[Constants.DATA_SIZE];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		this.socket.receive(packet);
		return new String(packet.getData());
	}

	public void sendData(String data, Runnable onSuccess, Consumer<Throwable> failedConsumer) {
		if (data.length() == 0) throw new IllegalArgumentException("cannot send empty data packet");
		String message = Base64.getEncoder().encodeToString(data.getBytes());
		this.sendData((message + "/e").getBytes(), onSuccess, failedConsumer);
	}

	private void sendData(final byte[] data, Runnable onSuccess, Consumer<Throwable> failedConsumer) {
		if (data.length > Constants.DATA_SIZE) {
			throw new IllegalArgumentException("data length cannot exceed " + Constants.DATA_SIZE + " bytes");
		}
		this.sendExecutorService.execute(() -> {
			DatagramPacket packet = new DatagramPacket(data, data.length, this.ipAddress, this.port);
			try {
				this.socket.send(packet);
			} catch (Exception ex) {
				if (failedConsumer != null) failedConsumer.accept(ex);
				else ex.printStackTrace();
				return;
			}
			if (onSuccess != null) onSuccess.run();
		});
	}

	public void sendPacket(Packet packet) {
		this.sendPacket(packet, null, null);
	}

	public void sendPacket(Packet packet, Runnable onSuccess, Consumer<Throwable> failedConsumer) {
		JsonObject jsonPacket = packet.toJson();
		jsonPacket.addProperty("t", packet.getId());
		this.sendData(jsonPacket.toString(), onSuccess, failedConsumer);
	}

	public void setReadTimeout(int timeout) {
		try {
			this.socket.setSoTimeout(timeout);
			this.timeout = timeout;
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
	}

	public void setUUID(UUID uuid) {
		if (this.uuid != null) throw new IllegalStateException("uuid already set");
		this.uuid = uuid;
	}

	public void shutdown(boolean wait) {
		if (wait) {
			this.sendExecutorService.shutdown();
			try {
				this.sendExecutorService.awaitTermination(1L, TimeUnit.SECONDS);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		} else {
			this.sendExecutorService.shutdownNow();
		}
	}

}
