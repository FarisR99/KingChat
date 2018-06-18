package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

public class PacketConnectionClient implements Packet {

	private final String name;

	public PacketConnectionClient(JsonObject jsonObject) {
		this.name = jsonObject.get("n").getAsString();
	}

	public PacketConnectionClient(String name) {
		this.name = name;
	}

	@Override
	public int getId() {
		return PacketType.Client.CONNECT;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("n", this.name);
		return jsonObject;
	}

}
