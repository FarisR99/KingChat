package com.faris.kingchat.server;

import java.net.InetAddress;
import java.util.*;

public class Client {

	private final UUID id;
	private int attempt = 0;

	private String name;
	private InetAddress address;
	private int port;

	public Client(UUID id, String name, InetAddress address, int port) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.port = port;
	}

	public InetAddress getAddress() {
		return this.address;
	}

	public int getAttempt() {
		return this.attempt;
	}

	public String getName() {
		return this.name;
	}

	public int getPort() {
		return this.port;
	}

	public UUID getUniqueId() {
		return this.id;
	}

	public void incrementAttempt() {
		this.attempt++;
	}

	public void resetAttempts() {
		this.attempt = 0;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ServerClient{" +
				"id=" + id +
				", name='" + name + '\'' +
				", address='" + address + '\'' +
				", port=" + port +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		Client that = (Client) o;
		return this.id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

}
