package com.faris.kingchat.server;

import com.faris.kingchat.core.config.FileConfiguration;
import com.faris.kingchat.core.config.YamlConfiguration;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import javafx.scene.image.Image;

import java.io.*;
import java.util.*;

public class ConfigManager {

	private static final JsonParser JSON_PARSER = new JsonParser();

	private final File dataFolder;
	private final File configFile;
	private final File bannedIPFile;
	private final File mutedIPFile;

	private FileConfiguration config = null;

	private JsonArray bannedIPs = new JsonArray();
	private JsonArray mutedIPs = new JsonArray();
	private String password = null;
	private String passwordOverride = null;
	private String serverIconURL = "";
	private List<Image> defaultProfilePictures = new ArrayList<>();

	public ConfigManager(File dataFolder) {
		this.dataFolder = dataFolder;
		this.configFile = this.getAbsoluteFile("config.yml");
		this.bannedIPFile = this.getAbsoluteFile("banned_ips.json");
		this.mutedIPFile = this.getAbsoluteFile("muted_ips.json");
	}

	public void loadConfig() {
		this.getConfig().addDefault("Password", "");
		this.getConfig().addDefault("Server icon URL", "");
		this.getConfig().addDefault("Default profile picture URLs", new ArrayList<>());
		this.saveConfig();

		String base64Password = this.config.getString("Password");
		this.password = base64Password != null && !base64Password.trim().isEmpty() ? new String(Base64.getDecoder().decode(base64Password)) : null;
		this.serverIconURL = this.config.getString("Server icon URL");
		if (this.serverIconURL != null) {
			if (this.serverIconURL.trim().isEmpty() || !((serverIconURL.startsWith("http://") || serverIconURL.startsWith("https://")) && (serverIconURL.endsWith(".png") || serverIconURL.endsWith(".jpg")))) {
				this.serverIconURL = null;
			}
		}

		this.defaultProfilePictures.clear();
		List<String> configDefaultProfilePicURLs = this.getConfig().getStringList("Default profile picture URLs");
		if (configDefaultProfilePicURLs.isEmpty()) {
			try {
				this.defaultProfilePictures.add(new Image(this.getClass().getResourceAsStream("/profile_picture_male.png")));
				this.defaultProfilePictures.add(new Image(this.getClass().getResourceAsStream("/profile_picture_female.png")));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			for (String configDefaultProfilePicURL : configDefaultProfilePicURLs) {
				try {
					this.defaultProfilePictures.add(new Image(configDefaultProfilePicURL));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
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

	public List<Image> getDefaultProfilePictures() {
		return this.defaultProfilePictures;
	}

	public String getPassword() {
		return this.passwordOverride != null ? this.passwordOverride : this.password;
	}

	public String getServerIconURL() {
		return this.serverIconURL;
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
			this.config.set("Password", Base64.getEncoder().encodeToString(this.password.getBytes()));
		} else {
			this.password = null;
			this.config.set("Password", null);
		}
		this.saveConfig();
	}

	public void setPasswordOverride(String passwordOverride) {
		this.passwordOverride = passwordOverride;
	}

	public FileConfiguration getConfig() {
		if (this.config == null || this.configFile == null) this.reloadConfig();
		return this.config;
	}

	private void reloadConfig() {
		this.config = YamlConfiguration.loadConfiguration(this.configFile);
	}

	private void saveConfig() {
		if (this.config == null) return;
		try {
			this.config.save(this.configFile);
		} catch (IOException ex) {
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
