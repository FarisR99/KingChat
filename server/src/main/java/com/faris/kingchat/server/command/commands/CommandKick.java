package com.faris.kingchat.server.command.commands;

import com.faris.kingchat.server.Server;
import com.faris.kingchat.server.Client;
import com.faris.kingchat.server.command.ServerCommand;

import java.util.*;

public class CommandKick extends ServerCommand {

	public CommandKick(Server server, String label, String[] args) {
		super(server, label, args);
	}

	@Override
	public boolean onCommand() {
		if (this.args.length == 1) {
			UUID clientUUID = null;
			try {
				clientUUID = UUID.fromString(this.args[0]);
			} catch (Exception ignored) {
			}
			Client targetClient = null;
			if (clientUUID == null) {
				targetClient = this.server.getClient(this.args[0]);
			} else {
				targetClient = this.server.getClient(clientUUID);
			}
			if (targetClient != null) {
				this.server.disconnectClient(targetClient.getUniqueId(), 2);
			} else {
				System.err.println("Unknown user: " + this.args[0]);
				if (this.server.getTerminal().hasGUI()) {
					this.server.getTerminal().getGUI().appendLine("Unknown user: " + this.args[0]);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public String getUsage() {
		return "<name|uuid>";
	}

}
