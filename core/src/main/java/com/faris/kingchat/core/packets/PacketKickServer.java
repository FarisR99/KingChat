package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

import java.util.*;

public class PacketKickServer implements Packet {

	private final String name;
	private final UUID uuid;

	public PacketKickServer(JsonObject jsonObject) {
		this.name = jsonObject.get("n").getAsString();
		this.uuid = UUID.fromString(jsonObject.get("u").getAsString());
	}

	public PacketKickServer(String name, UUID uuid) {
		this.name = name;
		this.uuid = uuid;
	}

	@Override
	public int getId() {
		return PacketType.Server.KICK;
	}

	public String getName() {
		return this.name;
	}

	public UUID getUUID() {
		return this.uuid;
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("n", this.name);
		jsonObject.addProperty("u", this.uuid.toString());
		return jsonObject;
	}

}
