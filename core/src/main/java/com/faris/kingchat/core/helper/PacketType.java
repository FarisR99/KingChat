package com.faris.kingchat.core.helper;

public class PacketType {

	public static class Client {
		public static final int CONNECT = 0;
		public static final int MESSAGE_SEND = 1;
		public static final int DISCONNECT = 2;
		public static final int PING = 3;
	}

	public static class Server {
		public static final int CONNECT = 0;
		public static final int MESSAGE_SEND = 1;
		public static final int DISCONNECT = 2;
		public static final int PING = 3;
		public static final int SHUTDOWN = 4;
		public static final int KICK = 5;
		public static final int USER_LIST = 6;
		public static final int MUTE = 7;
	}

}
