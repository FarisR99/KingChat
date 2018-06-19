package com.faris.kingchat.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class ConfigManager {

	private static final JsonParser JSON_PARSER = new JsonParser();

	private final File dataFolder;
	private File bannedIPFile;

	private JsonArray bannedIPs = new JsonArray();

	public ConfigManager(File dataFolder) {
		this.dataFolder = dataFolder;
		this.bannedIPFile = this.getRelativeFile("banned_ips.json").getAbsoluteFile();
	}

	public void loadBanList() {
		this.bannedIPs = new JsonArray();
		if (this.bannedIPFile.exists()) {
			try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.bannedIPFile))) {
				StringBuilder sbContent = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					sbContent.append(line).append(System.lineSeparator());
				}
				String content = sbContent.length() > 0 ? sbContent.substring(0, sbContent.length() - System.lineSeparator().length()) : "";
				if (!content.isEmpty()) {
					this.bannedIPs = JSON_PARSER.parse(content).getAsJsonArray();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void banIP(String ip) {
		JsonPrimitive jsonIP = new JsonPrimitive(ip);
		if (!this.bannedIPs.contains(jsonIP)) {
			this.bannedIPs.add(jsonIP);
			this.saveIPFile();
		}
	}

	public boolean isBanned(String ip) {
		return this.bannedIPs.contains(new JsonPrimitive(ip));
	}

	public void unbanIP(String ip) {
		JsonPrimitive jsonIP = new JsonPrimitive(ip);
		if (this.bannedIPs.contains(jsonIP)) {
			this.bannedIPs.remove(jsonIP);
			this.saveIPFile();
		}
	}

	private void saveIPFile() {
		try {
			if (!this.bannedIPFile.exists()) {
				if (!this.bannedIPFile.getParentFile().exists()) this.bannedIPFile.getParentFile().mkdirs();
				this.bannedIPFile.createNewFile();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		try (PrintWriter printWriter = new PrintWriter(this.bannedIPFile)) {
			printWriter.println(this.bannedIPs.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private File getRelativeFile(String path) {
		if (this.dataFolder != null) {
			return new File(this.dataFolder, path);
		} else {
			return new File(path);
		}
	}

}
