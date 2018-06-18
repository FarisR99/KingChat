package com.faris.kingchat.server.command.commands;

import com.faris.kingchat.server.Server;
import com.faris.kingchat.server.command.ServerCommand;

public class CommandStop extends ServerCommand {

	public CommandStop(Server server, String label, String[] args) {
		super(server, label, args);
	}

	@Override
	public boolean onCommand() {
		if (this.args.length == 0) {
			this.println("Received stop command. Shutting down...");
			this.server.shutdown();
			return true;
		}
		return false;
	}

	@Override
	public String getUsage() {
		return "";
	}

}
