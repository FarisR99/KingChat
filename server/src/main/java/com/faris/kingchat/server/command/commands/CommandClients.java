package com.faris.kingchat.server.command.commands;

import com.faris.kingchat.server.Client;
import com.faris.kingchat.server.Server;
import com.faris.kingchat.server.command.ServerCommand;

import java.util.*;

public class CommandClients extends ServerCommand {

	public CommandClients(Server server, String label, String[] args) {
		super(server, label, args);
	}

	@Override
	public boolean onCommand() {
		if (this.args.length == 0) {
			StringBuilder sbClients = new StringBuilder();
			List<Client> clients = this.server.getClients();
			for (int i = 0; i < clients.size(); i++) {
				Client client = clients.get(i);
				sbClients.append(client.getName()).append(" (").append(client.getUniqueId()).append(") @ ").append(client.getAddress().getHostName()).append(":").append(client.getPort());
				if (i != clients.size() - 1) sbClients.append(", ");
			}
			String output = "Clients: " + sbClients;
			System.out.println(output);
			if (this.server.getTerminal().hasGUI()) {
				this.server.getTerminal().getGUI().appendLine(output);
			}
			return true;
		}
		return false;
	}

	@Override
	public String getUsage() {
		return "";
	}

}
