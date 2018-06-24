package com.faris.kingchat.core.packets;

import com.faris.kingchat.core.helper.PacketType;
import com.google.gson.JsonObject;

public class PacketConnectionClient implements Packet {

	private final String name;
	private final String password;
	private final String profilePictureURL;

	public PacketConnectionClient(JsonObject jsonObject) {
		this.name = jsonObject.getAsJsonPrimitive("n").getAsString();
		this.password = jsonObject.has("p") ? jsonObject.getAsJsonPrimitive("p").getAsString() : null;
		this.profilePictureURL = jsonObject.has("i") ? jsonObject.getAsJsonPrimitive("i").getAsString() : null;
	}

	public PacketConnectionClient(String name, String password, String profilePictureURL) {
		this.name = name;
		this.password = password != null && !password.isEmpty() ? password : null;
		this.profilePictureURL = profilePictureURL;
	}

	@Override
	public int getId() {
		return PacketType.Client.CONNECT;
	}

	public String getName() {
		return this.name;
	}

	public String getPassword() {
		return this.password;
	}

	public String getProfilePictureURL() {
		return this.profilePictureURL;
	}

	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("n", this.name);
		if (this.password != null) jsonObject.addProperty("p", this.password);
		if (this.profilePictureURL != null) jsonObject.addProperty("i", this.profilePictureURL);
		return jsonObject;
	}

}
