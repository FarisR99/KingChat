package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

import java.util.*;

public class PacketKickServer implements Packet {

	private final String name;
	private final UUID uuid;
	private final long timestamp;

	public PacketKickServer(JsonObject jsonObject) {
		this.name = jsonObject.get("n").getAsString();
		this.uuid = UUID.fromString(jsonObject.get("u").getAsString());
		this.timestamp = jsonObject.get("t").getAsLong();
	}

	public PacketKickServer(String name, UUID uuid, long timestamp) {
		this.name = name;
		this.uuid = uuid;
		this.timestamp = timestamp;
	}

	@Override
	public int getId() {
		return PacketType.Server.KICK;
	}

	public String getName() {
		return this.name;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public UUID getUUID() {
		return this.uuid;
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("n", this.name);
		jsonObject.addProperty("u", this.uuid.toString());
		jsonObject.addProperty("t", this.timestamp);
		return jsonObject;
	}

}
