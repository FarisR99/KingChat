package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

public class PacketConnectionClient implements Packet {

	private final String name;
	private final String password;

	public PacketConnectionClient(JsonObject jsonObject) {
		this.name = jsonObject.getAsJsonPrimitive("n").getAsString();
		this.password = jsonObject.has("p") ? jsonObject.getAsJsonPrimitive("p").getAsString() : null;
	}

	public PacketConnectionClient(String name, String password) {
		this.name = name;
		this.password = password != null && !password.isEmpty() ? password : null;
	}

	@Override
	public int getId() {
		return PacketType.Client.CONNECT;
	}

	public String getName() {
		return this.name;
	}

	public String getPassword() {
		return this.password;
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("n", this.name);
		if (this.password != null) jsonObject.addProperty("p", this.password);
		return jsonObject;
	}

}
