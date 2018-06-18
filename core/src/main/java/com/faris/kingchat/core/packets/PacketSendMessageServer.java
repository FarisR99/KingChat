package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

import java.util.*;

public class PacketSendMessageServer implements Packet {

	private final String name;
	private final UUID senderUUID;
	private final String message;
	private final long timestamp;

	public PacketSendMessageServer(JsonObject jsonObject) {
		this.name = jsonObject.has("n") ? jsonObject.get("n").getAsString() : null;
		this.senderUUID = null;
		this.message = jsonObject.get("m").getAsString();
		this.timestamp = jsonObject.get("w").getAsLong();
	}

	public PacketSendMessageServer(String name, UUID sender, String message, long timestamp) {
		this.name = name;
		this.senderUUID = sender;
		this.message = message;
		this.timestamp = timestamp;
	}

	@Override
	public int getId() {
		return PacketType.Server.MESSAGE_SEND;
	}

	public String getMessage() {
		return this.message;
	}

	public String getName() {
		return this.name;
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
		if (this.name != null) jsonObject.addProperty("n", this.name);
		jsonObject.addProperty("m", this.message);
		jsonObject.addProperty("w", this.timestamp);
		return jsonObject;
	}

}
