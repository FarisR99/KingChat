package com.faris.kingchat.server.command.commands;

import com.faris.kingchat.server.Server;
import com.faris.kingchat.server.command.ServerCommand;

public class CommandPassword extends ServerCommand {

	public CommandPassword(Server server, String label, String[] args) {
		super(server, label, args);
	}

	@Override
	public boolean onCommand() {
		if (this.args.length == 1) {
			String password = this.args[0];
			if (password.equalsIgnoreCase("off")) {
				this.server.getConfigManager().setPassword(null);
			} else {
				this.server.getConfigManager().setPassword(password);
			}
			this.println("Set password to '" + password + "'.");
			return true;
		}
		return false;
	}

	@Override
	public String getUsage() {
		return "<password|off>";
	}

}
