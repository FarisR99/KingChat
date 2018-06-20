package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

import java.util.*;

public class PacketConnectionServer implements Packet {

	private final UUID uuid;
	private final String errorMessage;
	private final boolean muted;

	public PacketConnectionServer(JsonObject jsonObject) {
		this.errorMessage = jsonObject.has("e") ? jsonObject.getAsJsonPrimitive("e").getAsString() : null;
		this.uuid = this.errorMessage == null && jsonObject.has("u") ? UUID.fromString(jsonObject.getAsJsonPrimitive("u").getAsString()) : null;
		this.muted = jsonObject.has("m") && jsonObject.getAsJsonPrimitive("m").getAsBoolean();
	}

	public PacketConnectionServer(UUID uuid, boolean muted) {
		this(uuid, null, muted);
	}

	public PacketConnectionServer(String errorMessage) {
		this(null, errorMessage, false);
	}

	public PacketConnectionServer(UUID uuid, String errorMessage, boolean muted) {
		this.errorMessage = errorMessage;
		this.uuid = errorMessage == null ? uuid : null;
		this.muted = muted;
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

	public boolean isMuted() {
		return this.muted;
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		if (this.errorMessage != null) {
			jsonObject.addProperty("e", this.errorMessage);
		} else {
			jsonObject.addProperty("u", this.uuid.toString());
			jsonObject.addProperty("m", this.muted);
		}
		return jsonObject;
	}

	public boolean wasSuccessful() {
		return this.errorMessage == null;
	}

}
