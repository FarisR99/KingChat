package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

import java.util.*;

public class PacketPingClient implements Packet {

	private UUID uuid = null;
	private final long timestamp;

	public PacketPingClient(JsonObject jsonObject) {
		if (jsonObject.has("u")) {
			try {
				this.uuid = UUID.fromString(jsonObject.get("u").getAsString());
			} catch (Exception ignored) {
			}
		}
		this.timestamp = jsonObject.get("w").getAsLong();
	}

	public PacketPingClient(UUID clientUUID, long timestamp) {
		this.uuid = clientUUID;
		this.timestamp = timestamp;
	}

	@Override
	public int getId() {
		return PacketType.Client.PING;
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
		if (this.uuid != null) {
			jsonObject.addProperty("u", this.uuid.toString());
		}
		jsonObject.addProperty("w", this.timestamp);
		return jsonObject;
	}

}
