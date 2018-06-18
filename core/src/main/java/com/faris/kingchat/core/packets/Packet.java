package com.faris.kingchat.core.packets;

import com.google.gson.JsonObject;

public interface Packet {

	int getId();

	JsonObject toJson();

}
