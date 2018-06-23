package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

import java.util.*;

public class PacketConnectionServer implements Packet {

	private final UUID uuid;
	private final String errorMessage;
	private final boolean muted;
	private final String serverIconURL;

	public PacketConnectionServer(JsonObject jsonObject) {
		this.errorMessage = jsonObject.has("e") ? jsonObject.getAsJsonPrimitive("e").getAsString() : null;
		this.uuid = this.errorMessage == null && jsonObject.has("u") ? UUID.fromString(jsonObject.getAsJsonPrimitive("u").getAsString()) : null;
		this.muted = jsonObject.has("m") && jsonObject.getAsJsonPrimitive("m").getAsBoolean();
		this.serverIconURL = jsonObject.has("i") ? jsonObject.getAsJsonPrimitive("i").getAsString() : null;
	}

	public PacketConnectionServer(UUID uuid, boolean muted, String serverIconURL) {
		this(uuid, null, muted, serverIconURL);
	}

	public PacketConnectionServer(String errorMessage) {
		this(null, errorMessage, false, null);
	}

	public PacketConnectionServer(UUID uuid, String errorMessage, boolean muted, String serverIconURL) {
		this.errorMessage = errorMessage;
		this.uuid = errorMessage == null ? uuid : null;
		this.muted = muted;
		this.serverIconURL = serverIconURL;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	@Override
	public int getId() {
		return PacketType.Server.CONNECT;
	}

	public String getServerIconURL() {
		return this.serverIconURL;
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
			if (this.serverIconURL != null) jsonObject.addProperty("i", this.serverIconURL);
		}
		return jsonObject;
	}

	public boolean wasSuccessful() {
		return this.errorMessage == null;
	}

}
