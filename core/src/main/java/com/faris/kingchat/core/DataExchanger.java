package com.faris.kingchat.core;

import java.net.DatagramSocket;

public abstract class DataExchanger {

	protected final DatagramSocket socket;

	public DataExchanger(DatagramSocket socket) {
		this.socket = socket;
	}

	public void close(boolean async) {
		if (async) {
			new Thread(() -> {
				synchronized (this.socket) {
					if (this.socket.isConnected() && !this.socket.isClosed()) {
						this.socket.close();
					}
				}
			}, "SocketClose").start();
		} else {
			this.socket.close();
		}
	}

	public boolean isOpen() {
		return !this.socket.isClosed();
	}

}
