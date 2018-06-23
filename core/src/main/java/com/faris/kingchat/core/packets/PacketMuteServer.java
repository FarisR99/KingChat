package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

public class PacketMuteServer implements Packet {

	private final boolean muted;

	public PacketMuteServer(JsonObject jsonObject) {
		this.muted = jsonObject.getAsJsonPrimitive("m").getAsBoolean();
	}

	public PacketMuteServer(boolean muted) {
		this.muted = muted;
	}

	@Override
	public int getId() {
		return PacketType.Server.MUTE;
	}

	public boolean isMuted() {
		return this.muted;
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("m", this.muted);
		return jsonObject;
	}

}
