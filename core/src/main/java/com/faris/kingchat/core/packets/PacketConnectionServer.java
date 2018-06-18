package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

import java.util.*;

public class PacketConnectionServer implements Packet {

	private final UUID uuid;
	private final String errorMessage;

	public PacketConnectionServer(JsonObject jsonObject) {
		this.errorMessage = jsonObject.has("e") ? jsonObject.get("e").getAsString() : null;
		this.uuid = this.errorMessage == null && jsonObject.has("u") ? UUID.fromString(jsonObject.get("u").getAsString()) : null;
	}

	public PacketConnectionServer(UUID uuid, String errorMessage) {
		this.errorMessage = errorMessage;
		this.uuid = errorMessage == null ? uuid : null;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	@Override
	public int getId() {
		return PacketType.Server.CONNECT;
	}

	public UUID getUUID() {
		return this.uuid;
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		if (this.errorMessage != null) {
			jsonObject.addProperty("e", this.errorMessage);
		} else {
			jsonObject.addProperty("u", this.uuid.toString());
		}
		return jsonObject;
	}

	public boolean wasSuccessful() {
		return this.errorMessage == null;
	}

}
