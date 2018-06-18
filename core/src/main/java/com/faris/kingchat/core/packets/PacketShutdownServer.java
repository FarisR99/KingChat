package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

public class PacketShutdownServer implements Packet {

	@Override
	public int getId() {
		return PacketType.Server.SHUTDOWN;
	}

	@Override
	public JsonObject toJson() {
		return new JsonObject();
	}

}
