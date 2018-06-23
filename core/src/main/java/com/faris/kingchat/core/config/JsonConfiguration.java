package com.faris.kingchat.core.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class JsonConfiguration extends MapConfiguration {

	private static final ObjectMapper SORTED_MAPPER = new ObjectMapper();

	static {
		SORTED_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		SORTED_MAPPER.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		SORTED_MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		SORTED_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		SORTED_MAPPER.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
		SORTED_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
		SORTED_MAPPER.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
	}

	private JsonConfiguration() {
		this.configSection = new ConfigurationSection(SORTED_MAPPER.createObjectNode());
	}

	@Override
	public void load(File configFile) throws FileNotFoundException, JsonProcessingException, IOException {
		if (!configFile.exists()) throw new FileNotFoundException();
		this.configSection = new ConfigurationSection(SORTED_MAPPER.readTree(configFile));
	}

	@Override
	public void save(File destinationFile) throws IOException {
		this.saveFile(destinationFile);
		FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
		SORTED_MAPPER.getFactory().createGenerator(fileOutputStream).writeObject(this.configSection.getObjectNode());
	}

	public static JsonConfiguration loadConfiguration(File file) {
		JsonConfiguration jsonConfig = new JsonConfiguration();
		try {
			jsonConfig.load(file);
		} catch (FileNotFoundException ignored) {
		} catch (IOException ex) {
			System.err.println("Failed to load the configuration.");
			ex.printStackTrace();
		}
		return jsonConfig;
	}

}