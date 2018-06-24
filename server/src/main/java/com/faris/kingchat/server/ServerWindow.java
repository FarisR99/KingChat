package com.faris.kingchat.server;


import com.faris.kingchat.core.helper.PrettyLogger;
import com.faris.kingchat.core.helper.Utilities;

import java.util.*;
import java.util.logging.*;

public class ServerWindow {

	private final Server server;
	private Logger serverLogger;
	private final ServerGUI gui;

	public ServerWindow(int port, String password, ServerGUI gui) throws Exception {
		this.gui = gui;
		this.serverLogger = PrettyLogger.createLogger("ServerLog");
		this.server = new Server(this, port, password);
	}

	public ServerGUI getGUI() {
		return this.gui;
	}

	public Logger getLogger() {
		return this.serverLogger;
	}

	public Server getServer() {
		return this.server;
	}

	public boolean hasGUI() {
		return this.gui != null;
	}

	public static void main(String[] args) throws Exception {
		boolean gui = true, customPort = false;
		int port = 8192;
		String password = null;
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].isEmpty()) continue;
				String arg = args[i];
				if (arg.equalsIgnoreCase("--port")) {
					if (i != args.length - 1) {
						OptionalInt optionalPort = Utilities.parseInt(args[i + 1]);
						if (optionalPort.isPresent()) {
							if (optionalPort.getAsInt() >= 0) {
								port = optionalPort.getAsInt();
								customPort = true;
							}
						} else {
							throw new IllegalArgumentException("invalid port '" + args[i + 1] + "' in run arguments");
						}
					} else {
						throw new IllegalArgumentException("empty port in run arguments");
					}
				} else if (arg.equalsIgnoreCase("--nogui")) {
					gui = false;
				} else if (arg.equalsIgnoreCase("--password")) {
					if (i != args.length - 1) {
						password = args[i + 1];
					} else {
						throw new IllegalArgumentException("empty password in run arguments");
					}
				}
			}
		}
		if (gui) {
			ServerGUI.launch(ServerGUI.class, String.valueOf(port), String.valueOf(customPort), password);
		} else {
			if (!customPort) {
				System.out.print("Please enter the port for the server to run on: ");
				Scanner scanner = new Scanner(System.in);
				String strPort = scanner.nextLine();
				OptionalInt optionalPort = Utilities.parseInt(strPort);
				if (optionalPort.isPresent()) {
					port = optionalPort.getAsInt();
				}
			}
			if (port < 0) port = 8192;
			new ServerWindow(port, password, null);
		}
	}

}
