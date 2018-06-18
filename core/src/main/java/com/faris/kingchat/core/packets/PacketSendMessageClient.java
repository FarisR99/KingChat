package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

import java.util.*;

public class PacketSendMessageClient implements Packet {

	private UUID senderUUID;
	private final String message;
	private final long timestamp;

	public PacketSendMessageClient(JsonObject jsonObject) {
		try {
			this.senderUUID = UUID.fromString(jsonObject.get("u").getAsString());
		} catch (Exception ignored) {
		}
		this.message = jsonObject.get("m").getAsString();
		this.timestamp = jsonObject.get("w").getAsLong();
	}

	public PacketSendMessageClient(UUID senderUUID, String message, long timestamp) {
		this.senderUUID = senderUUID;
		this.message = message;
		this.timestamp = timestamp;
	}

	@Override
	public int getId() {
		return PacketType.Client.MESSAGE_SEND;
	}

	public String getMessage() {
		return this.message;
	}

	public UUID getSenderUUID() {
		return this.senderUUID;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("u", this.senderUUID.toString());
		jsonObject.addProperty("m", this.message);
		jsonObject.addProperty("w", this.timestamp);
		return jsonObject;
	}

}
