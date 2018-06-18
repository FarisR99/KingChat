package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

public class PacketPingServer implements Packet {

	private final long timestamp;

	public PacketPingServer(JsonObject jsonObject) {
		this.timestamp = jsonObject.get("w").getAsLong();
	}

	public PacketPingServer(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int getId() {
		return PacketType.Server.PING;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("w", this.timestamp);
		return jsonObject;
	}

}
