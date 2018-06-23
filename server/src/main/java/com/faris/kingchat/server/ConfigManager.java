package com.faris.kingchat.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.*;
import java.util.*;

public class ConfigManager {

	private static final JsonParser JSON_PARSER = new JsonParser();

	private final File dataFolder;
	private final File configFile;
	private final File bannedIPFile;
	private final File mutedIPFile;

	private Properties configProperties = null;

	private JsonArray bannedIPs = new JsonArray();
	private JsonArray mutedIPs = new JsonArray();
	private String password = null;
	private String passwordOverride = null;

	public ConfigManager(File dataFolder) {
		this.dataFolder = dataFolder;
		this.configFile = this.getAbsoluteFile("config.properties");
		this.bannedIPFile = this.getAbsoluteFile("banned_ips.json");
		this.mutedIPFile = this.getAbsoluteFile("muted_ips.json");
	}

	public void loadConfig() throws Exception {
		this.configProperties = new Properties();
		if (this.configFile.exists()) this.configProperties.load(new FileReader(this.configFile));

		String base64Password = this.configProperties.getProperty("Password", null);
		if (base64Password != null && !base64Password.equals("null")) {
			this.password = new String(Base64.getDecoder().decode(base64Password));
		} else {
			this.password = null;
		}
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

	public void loadMutedList() {
		this.mutedIPs = new JsonArray();
		if (this.mutedIPFile.exists()) {
			try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.mutedIPFile))) {
				StringBuilder sbContent = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					sbContent.append(line).append(System.lineSeparator());
				}
				String content = sbContent.length() > 0 ? sbContent.substring(0, sbContent.length() - System.lineSeparator().length()) : "";
				if (!content.isEmpty()) {
					this.mutedIPs = JSON_PARSER.parse(content).getAsJsonArray();
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
			this.saveBannedIPFile();
		}
	}

	public String getPassword() {
		return this.passwordOverride != null ? this.passwordOverride : this.password;
	}

	public boolean isBanned(String ip) {
		return this.bannedIPs.contains(new JsonPrimitive(ip));
	}

	public boolean isMuted(String ip) {
		return this.mutedIPs.contains(new JsonPrimitive(ip));
	}

	public void muteIP(String ip) {
		JsonPrimitive jsonIP = new JsonPrimitive(ip);
		if (!this.mutedIPs.contains(jsonIP)) {
			this.mutedIPs.add(jsonIP);
			this.saveMutedIPFile();
		}
	}

	public void unbanIP(String ip) {
		JsonPrimitive jsonIP = new JsonPrimitive(ip);
		if (this.bannedIPs.contains(jsonIP)) {
			this.bannedIPs.remove(jsonIP);
			this.saveBannedIPFile();
		}
	}

	public void unmuteIP(String ip) {
		JsonPrimitive jsonIP = new JsonPrimitive(ip);
		if (this.mutedIPs.contains(jsonIP)) {
			this.mutedIPs.remove(jsonIP);
			this.saveMutedIPFile();
		}
	}

	private void saveBannedIPFile() {
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

	private void saveMutedIPFile() {
		try {
			if (!this.mutedIPFile.exists()) {
				if (!this.mutedIPFile.getParentFile().exists()) this.mutedIPFile.getParentFile().mkdirs();
				this.mutedIPFile.createNewFile();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		try (PrintWriter printWriter = new PrintWriter(this.mutedIPFile)) {
			printWriter.println(this.mutedIPs.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setPassword(String password) {
		if (password != null) {
			this.password = password;
			this.configProperties.setProperty("Password", Base64.getEncoder().encodeToString(this.password.getBytes()));
		} else {
			this.password = null;
			this.configProperties.remove("Password");
		}
		this.saveConfig();
	}

	public void setPasswordOverride(String passwordOverride) {
		this.passwordOverride = passwordOverride;
	}

	private void saveConfig() {
		try {
			if (!this.configFile.exists()) {
				if (!this.configFile.getParentFile().exists()) {
					this.configFile.getParentFile().mkdirs();
				}
				this.configFile.createNewFile();
			}
			try (FileOutputStream fileOutputStream = new FileOutputStream(this.configFile)) {
				this.configProperties.store(fileOutputStream, null);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private File getAbsoluteFile(String path) {
		if (this.dataFolder != null) {
			return new File(this.dataFolder, path).getAbsoluteFile();
		} else {
			return new File(path).getAbsoluteFile();
		}
	}

}
