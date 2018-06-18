package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PacketUserListServer implements Packet {

	private final String[] users;

	public PacketUserListServer(JsonObject jsonObject) {
		JsonArray usersArray = jsonObject.getAsJsonArray("u");
		this.users = new String[usersArray.size()];
		for (int i = 0; i < this.users.length; i++) {
			this.users[i] = usersArray.get(i).getAsString();
		}
	}

	public PacketUserListServer(String[] users) {
		this.users = users;
	}

	@Override
	public int getId() {
		return PacketType.Server.USER_LIST;
	}

	public String[] getUsers() {
		return this.users;
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		JsonArray usersArray = new JsonArray();
		for (String user : this.users) usersArray.add(user);
		jsonObject.add("u", usersArray);
		return jsonObject;
	}
}
