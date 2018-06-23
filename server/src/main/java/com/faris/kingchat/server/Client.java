package com.faris.kingchat.server;

import java.net.InetAddress;
import java.text.DateFormat;
import java.util.*;

public class Client {

	private final static DateFormat INFO_DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

	private final UUID id;
	private int attempt = 0;

	private String name;
	private InetAddress address;
	private int port;

	private final long connectTime;

	public Client(UUID id, String name, InetAddress address, int port) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.port = port;

		this.connectTime = System.currentTimeMillis();
	}

	public InetAddress getAddress() {
		return this.address;
	}

	public int getAttempt() {
		return this.attempt;
	}

	public long getConnectTime() {
		return this.connectTime;
	}

	public String getInfo() {
		StringBuilder sbInfo = new StringBuilder("Name: ");
		sbInfo.append(this.name).append(System.lineSeparator());
		sbInfo.append("ID: ").append(this.id).append(System.lineSeparator());
		sbInfo.append("IP: ").append(this.address.getHostName()).append(":").append(this.port).append(System.lineSeparator());
		sbInfo.append("Connected on: ").append(INFO_DATE_FORMAT.format(new Date(this.connectTime)));
		return sbInfo.toString();
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
		return "Client{" +
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