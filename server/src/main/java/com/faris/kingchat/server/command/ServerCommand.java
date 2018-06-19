package com.faris.kingchat.server.command;

import com.faris.kingchat.core.helper.Utilities;
import com.faris.kingchat.server.Server;
import com.faris.kingchat.server.command.commands.CommandClients;
import com.faris.kingchat.server.command.commands.CommandKick;
import com.faris.kingchat.server.command.commands.CommandStop;

import java.util.*;

public abstract class ServerCommand {

	private static final Map<String, Class<? extends ServerCommand>> commandMap = new HashMap<>();

	static {
		commandMap.put("stop", CommandStop.class);
		commandMap.put("clients", CommandClients.class);
		commandMap.put("kick", CommandKick.class);
	}

	protected final Server server;
	protected final String label;
	protected final String[] args;

	public ServerCommand(Server server, String label, String[] args) {
		this.server = server;
		this.label = label;
		this.args = args;
	}

	public abstract boolean onCommand();

	public abstract String getUsage();

	public static Class<? extends ServerCommand> getCommandClass(String command) {
		return command != null ? commandMap.get(command.toLowerCase()) : null;
	}

	protected void print(String message) {
		System.out.print(message);
		if (this.server.getTerminal().hasGUI()) this.server.getTerminal().getGUI().append(message);
	}

	protected void println(String message) {
		System.out.println(message);
		if (this.server.getTerminal().hasGUI()) this.server.getTerminal().getGUI().appendLine(message);
	}

	protected void printError(String message) {
		System.err.print(message);
		if (this.server.getTerminal().hasGUI()) this.server.getTerminal().getGUI().append(message);
	}

	protected void printlnError(String message) {
		System.err.println(message);
		if (this.server.getTerminal().hasGUI()) this.server.getTerminal().getGUI().appendLine(message);
	}

	protected void printThrowable(Throwable throwable) {
		throwable.printStackTrace();
		if (this.server.getTerminal().hasGUI()) {
			this.server.getTerminal().getGUI().appendLine(Utilities.getThrowableAsString(throwable));
		}
	}

}
