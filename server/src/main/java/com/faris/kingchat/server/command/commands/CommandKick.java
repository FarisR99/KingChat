package com.faris.kingchat.server.command.commands;

import com.faris.kingchat.core.helper.Utilities;
import com.faris.kingchat.server.Client;
import com.faris.kingchat.server.Server;
import com.faris.kingchat.server.command.ServerCommand;

import java.util.*;
import java.util.regex.*;

public class CommandKick extends ServerCommand {

	private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("\\d(\\d)?(\\d)?.\\d(\\d)?(\\d)?.\\d(\\d)?(\\d)?.\\d(\\d)?(\\d)?(:\\d+)?");

	public CommandKick(Server server, String label, String[] args) {
		super(server, label, args);
	}

	@Override
	public boolean onCommand() {
		if (this.args.length == 1) {
			List<Client> targetClients = new ArrayList<>();
			Client targetClient = this.server.getClient(this.args[0]);
			boolean isIPAddress = false;
			if (targetClient == null) {
				try {
					targetClient = this.server.getClient(UUID.fromString(this.args[0]));
				} catch (Exception ignored) {
				}
				if (targetClient == null) {
					isIPAddress = IP_ADDRESS_PATTERN.matcher(this.args[0]).matches();
					if (isIPAddress) {
						if (this.args[0].contains(":")) {
							String[] ipSplit = this.args[0].split(":");
							OptionalInt optionalPort = Utilities.parseInt(ipSplit[1]);
							if (optionalPort.isPresent()) {
								targetClient = this.server.getClientByIP(ipSplit[0], optionalPort.getAsInt());
							}
						} else {
							targetClients.addAll(this.server.getClientsByIP(this.args[0]));
						}
					}
				}
			}
			if (targetClient != null) targetClients.add(targetClient);
			if (!targetClients.isEmpty()) {
				for (Client client : targetClients) {
					this.server.disconnectClient(client.getUniqueId(), 2);
				}
				this.println("Kicked " + targetClients.size() + " client" + (targetClients.size() != 1 ? "s" : "") + ".");
			} else {
				this.printlnError("Unknown " + (isIPAddress ? "IP" : "user") + ": " + this.args[0]);
			}
			return true;
		}
		return false;
	}

	@Override
	public String getUsage() {
		return "<name|uuid|ip:[port]>";
	}

}
