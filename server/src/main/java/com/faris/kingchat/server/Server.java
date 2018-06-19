package com.faris.kingchat.server;

import com.faris.kingchat.core.helper.PacketType;
import com.faris.kingchat.core.helper.Utilities;
import com.faris.kingchat.core.packets.*;
import com.faris.kingchat.server.command.ServerCommand;
import com.google.gson.JsonObject;
import javafx.application.Platform;

import java.lang.reflect.Constructor;
import java.net.DatagramPacket;
import java.util.*;
import java.util.logging.*;

public class Server implements Runnable {

	private static final int MAX_CLIENTS = 1000;
	private static final int MAX_ATTEMPTS = 3;

	private final ServerWindow terminal;
	private final int port;
	private final ServerDataExchanger dataExchanger;

	private Thread runningThread;
	private volatile boolean running;
	private Thread clientsThread;

	private final Map<UUID, Client> clients = new HashMap<>();
	private final List<UUID> clientResponse = new ArrayList<>();
	private long lastPing = -1L;
	private final Map<String, Long> lastKick = new HashMap<>();

	private ConfigManager configManager = null;

	public Server(ServerWindow terminal, int port) throws Exception {
		this.terminal = terminal;
		this.port = port;
		this.dataExchanger = new ServerDataExchanger(this);

		this.configManager = new ConfigManager(null);
		this.configManager.loadBanList();

		this.runningThread = new Thread(this, "Server");
		this.runningThread.start();
	}

	@Override
	public void run() {
		this.running = true;
		this.terminal.getLogger().log(Level.INFO, "Server started on port " + this.port);
		this.manageClients();
		this.dataExchanger.receive();
		if (!this.terminal.hasGUI()) {
			Scanner scanner = new Scanner(System.in);
			while (this.running) {
				try {
					String input = scanner.nextLine();
					this.processInput(input);
				} catch (Exception ex) {
					ex.printStackTrace();
					break;
				}
			}
		}
	}

	public List<Client> banIP(String ipAddress) {
		List<Client> clientList = this.getClientsByIP(ipAddress);
		for (Client client : clientList) {
			this.disconnectClient(client.getUniqueId(), 2);
		}
		this.configManager.banIP(ipAddress);
		return clientList;
	}

	private void broadcastMessage(Client clientSender, String message, long timestamp) {
		List<Client> clients = new ArrayList<>(this.clients.values());
		PacketSendMessageServer messagePacket = new PacketSendMessageServer(clientSender.getName(), clientSender.getUniqueId(), message, timestamp);
		this.dataExchanger.sendPacket(messagePacket, clients, null, null);
	}

	public void destroyServer() {
		if (!this.running) return;
		this.running = false;
		this.clients.clear();
		if (this.dataExchanger != null) {
			try {
				this.dataExchanger.close(false);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void disconnectClient(UUID clientUUID, int status) {
		Client client = this.clients.remove(clientUUID);
		if (client == null) return;
		if (this.getTerminal().hasGUI()) this.getTerminal().getGUI().removeUser(client);
		if (status == 0) {
			this.terminal.getLogger().log(Level.INFO, "Client " + client.getName() + " (" + client.getUniqueId() + ") @ " + client.getAddress() + ":" + client.getPort() + " disconnected.");
		} else if (status == 1) {
			this.terminal.getLogger().log(Level.INFO, "Client " + client.getName() + " (" + client.getUniqueId() + ") @ " + client.getAddress() + ":" + client.getPort() + " timed out.");
		} else if (status == 2) {
			this.terminal.getLogger().log(Level.INFO, "Client " + client.getName() + " (" + client.getUniqueId() + ") @ " + client.getAddress() + ":" + client.getPort() + " has been kicked.");
		}

		long timestamp = System.currentTimeMillis();
		List<Client> clients = new ArrayList<>(this.clients.values());
		PacketDisconnectServer disconnectPacket = new PacketDisconnectServer(client.getName(), client.getUniqueId(), timestamp);
		this.dataExchanger.sendPacket(disconnectPacket, clients, null, null);

		if (status == 2) {
			this.lastKick.put(client.getAddress().getHostName(), System.currentTimeMillis());

			PacketKickServer kickPacket = new PacketKickServer(client.getName(), client.getUniqueId());
			this.dataExchanger.sendPacket(kickPacket, clients);
			this.dataExchanger.sendPacket(kickPacket, client.getAddress(), client.getPort());
		}
	}

	public Client getClient(String name) {
		if (name == null) return null;
		for (Client client : this.clients.values()) {
			if (client.getName().equalsIgnoreCase(name)) return client;
		}
		return null;
	}

	public Client getClient(UUID uuid) {
		return this.clients.get(uuid);
	}

	public Client getClientByIP(String ipAddress, int port) {
		for (Client client : this.clients.values()) {
			if (client.getAddress().getHostName().equals(ipAddress) && client.getPort() == port) {
				return client;
			}
		}
		return null;
	}

	public List<Client> getClients() {
		return new ArrayList<>(this.clients.values());
	}

	public List<Client> getClientsByIP(String ipAddress) {
		List<Client> clients = new ArrayList<>();
		for (Client client : this.clients.values()) {
			if (client.getAddress().getHostName().equals(ipAddress)) {
				clients.add(client);
			}
		}
		return clients;
	}

	public ConfigManager getConfigManager() {
		return this.configManager;
	}

	public ServerDataExchanger getDataExchanger() {
		return this.dataExchanger;
	}

	private List<PacketDisconnectServer> getDisconnectPackets(Collection<Client> clients, boolean status) {
		List<PacketDisconnectServer> disconnectPackets = new ArrayList<>();
		for (Client client : clients) {
			String logMessage;
			if (status) {
				logMessage = "Client " + client.getName() + " (" + client.getUniqueId() + ") @ " + client.getAddress() + ":" + client.getPort() + " disconnected.";
			} else {
				logMessage = "Client " + client.getName() + " (" + client.getUniqueId() + ") @ " + client.getAddress() + ":" + client.getPort() + " timed out.";
			}
			this.terminal.getLogger().log(Level.INFO, logMessage);

			long timestamp = System.currentTimeMillis();
			PacketDisconnectServer disconnectPacket = new PacketDisconnectServer(client.getName(), client.getUniqueId(), timestamp);
			disconnectPackets.add(disconnectPacket);
		}
		return disconnectPackets;
	}

	public int getPort() {
		return this.port;
	}

	public ServerWindow getTerminal() {
		return this.terminal;
	}

	public boolean isRunning() {
		return this.running;
	}

	private void manageClients() {
		this.clientsThread = new Thread(() -> {
			clientLoop:
			while (this.running) {
				synchronized (this.clients) {
					List<Client> connectedClients = new ArrayList<>(this.clients.values());

					PacketPingServer pingPacket = new PacketPingServer(System.currentTimeMillis());
					this.lastPing = pingPacket.getTimestamp();
					this.dataExchanger.sendPacket(pingPacket, connectedClients);

					String[] users = new String[connectedClients.size()];
					int i = 0;
					for (Client client : connectedClients) {
						users[i++] = client.getName();
						if (!this.running) break clientLoop;
					}
					PacketUserListServer userListPacket = new PacketUserListServer(users);
					this.dataExchanger.sendPacket(userListPacket, connectedClients);

					while (System.currentTimeMillis() - this.lastPing < 2000L) {
						if (!this.running) break clientLoop;
					}
					connectedClients = new ArrayList<>(this.clients.values());

					List<Client> inactiveClients = new ArrayList<>();
					for (Client client : connectedClients) {
						if (!this.clientResponse.contains(client.getUniqueId())) {
							if (client.getAttempt() >= MAX_ATTEMPTS) {
								inactiveClients.add(client);
							} else {
								client.incrementAttempt();
							}
						} else {
							this.clientResponse.remove(client.getUniqueId());
							client.resetAttempts();
						}
					}
					for (Client client : inactiveClients) this.clients.remove(client.getUniqueId());
					List<PacketDisconnectServer> disconnectPackets = this.getDisconnectPackets(inactiveClients, false);
					for (PacketDisconnectServer disconnectPacket : disconnectPackets) {
						this.dataExchanger.sendPacket(disconnectPacket, connectedClients);
					}
				}
			}
		}, "Clients");
		this.clientsThread.start();
	}

	public void processInput(String input) {
		if (input.startsWith("/")) {
			String[] inputSplit = input.split("\\s+");
			String command = inputSplit[0].substring(1);
			String[] args = new String[inputSplit.length - 1];
			System.arraycopy(inputSplit, 1, args, 0, args.length);
			try {
				Class<? extends ServerCommand> commandClass = ServerCommand.getCommandClass(command);
				if (commandClass != null) {
					Constructor<ServerCommand> commandConstructor = (Constructor<ServerCommand>) commandClass.getConstructors()[0];
					ServerCommand serverCommand = commandConstructor.newInstance(this, command, args);
					if (!serverCommand.onCommand()) {
						String usageMessage = "Usage: /" + command + " " + serverCommand.getUsage();
						System.err.println(usageMessage);
						if (this.terminal.hasGUI()) {
							this.terminal.getGUI().appendLine(usageMessage);
						}
					}
				} else {
					System.err.println("Unknown command: " + command);
					if (this.terminal.hasGUI()) this.terminal.getGUI().appendLine("Unknown command: " + command);
				}
			} catch (Exception ex) {
				String errorMsg = "An error occurred whilst trying to execute '" + command + "':";
				System.err.println(errorMsg);
				if (this.terminal.hasGUI()) this.terminal.getGUI().appendLine(input);
				String error = Utilities.getThrowableAsString(ex);
				System.err.println(error);
				if (this.terminal.hasGUI()) this.terminal.getGUI().appendLine(error);
			}
		} else {
			this.dataExchanger.sendPacket(new PacketSendMessageServer(null, null, input, System.currentTimeMillis()), this.clients.values());
			System.out.println(input);
			this.terminal.getGUI().appendLine(input);
		}
	}

	protected void processPacketReceived(DatagramPacket packet) {
		String dataIn = new String(packet.getData());
		try {
			String trimmedData = Utilities.trimData(dataIn);
			if (trimmedData == null) {
				this.terminal.getLogger().severe("Received invalid data from " + packet.getAddress() + ":" + packet.getPort());
				System.out.println("Invalid data: " + dataIn);
				return;
			}
			String msgIn = new String(Base64.getDecoder().decode(trimmedData.getBytes()));
			if (msgIn.startsWith("{") && msgIn.endsWith("}")) {
				JsonObject jsonMessage = Utilities.getGson().fromJson(msgIn, JsonObject.class);
				if (jsonMessage.has("t")) {
					int packetType = jsonMessage.get("t").getAsInt();
					try {
						long receiveTime = System.currentTimeMillis();
						if (packetType == PacketType.Client.CONNECT) {
							if (MAX_CLIENTS != -1 && this.clients.size() >= MAX_CLIENTS) {
								PacketConnectionServer connectPacketResponse = new PacketConnectionServer(null, "Server reached maximum user limit (" + MAX_CLIENTS + ")");
								this.dataExchanger.sendPacket(connectPacketResponse, packet.getAddress(), packet.getPort());
								return;
							}
							PacketConnectionClient connectPacket = new PacketConnectionClient(jsonMessage);
							if (this.configManager.isBanned(packet.getAddress().getHostName())) {
								PacketConnectionServer connectPacketResponse = new PacketConnectionServer(null, "You are banned!");
								this.dataExchanger.sendPacket(connectPacketResponse, packet.getAddress(), packet.getPort());
								return;
							}
							if (this.lastKick.containsKey(packet.getAddress().getHostName())) {
								long lastKickTimestamp = this.lastKick.get(packet.getAddress().getHostName());
								if (System.currentTimeMillis() - lastKickTimestamp < 5000L) {
									PacketConnectionServer connectPacketResponse = new PacketConnectionServer(null, "You have been kicked recently, please try reconnecting later.");
									this.dataExchanger.sendPacket(connectPacketResponse, packet.getAddress(), packet.getPort());
									return;
								}
							}
							if (connectPacket.getName().isEmpty() || connectPacket.getName().length() > 16 || !Utilities.VALID_USERNAME_PATTERN.matcher(connectPacket.getName()).matches()) {
								PacketConnectionServer connectPacketResponse = new PacketConnectionServer(null, "Invalid username '" + connectPacket.getName() + "'");
								this.dataExchanger.sendPacket(connectPacketResponse, packet.getAddress(), packet.getPort());
								return;
							}
							for (Client client : this.clients.values()) {
								if (client.getName().equalsIgnoreCase(connectPacket.getName())) {
									PacketConnectionServer connectPacketResponse = new PacketConnectionServer(null, "Name already in use by '" + client.getName() + "'");
									this.dataExchanger.sendPacket(connectPacketResponse, packet.getAddress(), packet.getPort());
									return;
								}
							}
							Client client = new Client(UUID.randomUUID(), connectPacket.getName(), packet.getAddress(), packet.getPort());
							this.clients.put(client.getUniqueId(), client);
							this.terminal.getLogger().log(Level.INFO, "Received connection from client " + connectPacket.getName() + " (" + client.getUniqueId() + ") at address " + client.getAddress() + ":" + client.getPort() + "");
							PacketConnectionServer connectPacketResponse = new PacketConnectionServer(client.getUniqueId(), null);
							this.dataExchanger.sendPacket(connectPacketResponse, client.getAddress(), client.getPort(), () -> {
								if (this.terminal.hasGUI()) this.terminal.getGUI().addUser(client);
							}, throwable -> {
								this.terminal.getLogger().log(Level.WARNING, "Lost connection to client " + client.getName() + " (" + client.getAddress() + ":" + client.getPort() + ")", throwable);
								this.clients.remove(client.getUniqueId());
							});
						} else if (packetType == PacketType.Client.MESSAGE_SEND) {
							PacketSendMessageClient messagePacket = new PacketSendMessageClient(jsonMessage);
							if (messagePacket.getSenderUUID() == null || receiveTime < messagePacket.getTimestamp()) {
								return;
							}
							Client client = this.clients.get(messagePacket.getSenderUUID());
							if (client == null) {
								return;
							}
							if (!client.getAddress().equals(packet.getAddress()) || client.getPort() != packet.getPort()) {
								return;
							}
							this.terminal.getLogger().log(Level.INFO, "Received message from " + client.getName() + ": " + messagePacket.getMessage());
							this.broadcastMessage(client, messagePacket.getMessage(), messagePacket.getTimestamp());
						} else if (packetType == PacketType.Client.DISCONNECT) {
							PacketDisconnectClient disconnectPacket = new PacketDisconnectClient(jsonMessage);
							if (disconnectPacket.getUUID() == null || receiveTime < disconnectPacket.getTimestamp()) {
								return;
							}
							Client client = this.clients.get(disconnectPacket.getUUID());
							if (client == null) {
								return;
							}
							if (!client.getAddress().equals(packet.getAddress()) || client.getPort() != packet.getPort()) {
								return;
							}
							this.disconnectClient(disconnectPacket.getUUID(), 0);
						} else if (packetType == PacketType.Client.PING) {
							PacketPingClient pingPacket = new PacketPingClient(jsonMessage);
							if (pingPacket.getUUID() == null) {
								return;
							}
							Client client = this.clients.get(pingPacket.getUUID());
							if (client == null) {
								return;
							}
							if (!client.getAddress().equals(packet.getAddress()) || client.getPort() != packet.getPort()) {
								return;
							}
							if (pingPacket.getTimestamp() == this.lastPing) {
								this.clientResponse.add(client.getUniqueId());
							} else {
								this.disconnectClient(client.getUniqueId(), 1);
							}
						}
					} catch (Exception ex) {
						this.terminal.getLogger().log(Level.SEVERE, "Failed to process packet data '" + dataIn + "'", ex);
					}
				}
			}
		} catch (Exception ignored) {
		}
	}

	public void setRunning(boolean flag) {
		this.running = flag;
	}

	public void shutdown() {
		if (!this.running) return;
		Runnable destroyRunnable = () -> {
			this.running = false;
			if (this.terminal.hasGUI()) {
				Platform.runLater(() -> {
					this.terminal.getGUI().close();
					Platform.exit();
					System.exit(0);
				});
			}
			if (this.dataExchanger != null) {
				try {
					this.dataExchanger.close(false);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			synchronized (this.clients) {
				this.clients.clear();
			}
		};
		if (this.dataExchanger != null && this.dataExchanger.isOpen()) {
			try {
				this.dataExchanger.sendPacket(new PacketShutdownServer(), this.clients.values(), destroyRunnable);
			} catch (Exception ex) {
				ex.printStackTrace();
				destroyRunnable.run();
			}
		} else {
			destroyRunnable.run();
		}
	}

}
