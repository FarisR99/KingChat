package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

import java.util.*;

public class PacketDisconnectClient implements Packet {

	private UUID uuid;
	private final long timestamp;

	public PacketDisconnectClient(JsonObject jsonObject) {
		if (jsonObject.has("u")) {
			try {
				this.uuid = UUID.fromString(jsonObject.get("u").getAsString());
			} catch (Exception ignored) {
			}
		}
		this.timestamp = jsonObject.get("w").getAsLong();
	}

	public PacketDisconnectClient(UUID uuid, long timestamp) {
		this.uuid = uuid;
		this.timestamp = timestamp;
	}

	@Override
	public int getId() {
		return PacketType.Client.DISCONNECT;
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
		jsonObject.addProperty("u", this.uuid.toString());
		jsonObject.addProperty("w", this.timestamp);
		return jsonObject;
	}

}
