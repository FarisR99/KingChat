package com.faris.kingchat.core.config;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class MapConfiguration extends FileConfiguration {

	protected ConfigurationSection configSection = null;

	protected MapConfiguration() {
	}

	@Override
	public void addDefault(String path, Object value) {
		this.configSection.addDefault(path, value);
	}

	@Override
	public boolean contains(String path) {
		return this.configSection.contains(path);
	}

	@Override
	public ConfigurationSection createSection(String path) {
		return this.configSection.createSection(path);
	}

	@Override
	public Object get(String path) {
		return this.configSection.get(path);
	}

	@Override
	public Object get(String path, Object defaultValue) {
		return this.configSection.get(path, defaultValue);
	}

	@Override
	public byte getByte(String path) {
		return this.configSection.getByte(path);
	}

	@Override
	public byte getByte(String path, Byte defaultValue) {
		return this.configSection.getByte(path, defaultValue);
	}

	@Override
	public ConfigurationSection getConfigurationSection(String path) {
		return this.configSection.getConfigurationSection(path);
	}

	@Override
	public double getDouble(String path) {
		return this.configSection.getDouble(path);
	}

	@Override
	public double getDouble(String path, Double defaultValue) {
		return this.configSection.getDouble(path, defaultValue);
	}

	@Override
	public float getFloat(String path) {
		return this.configSection.getFloat(path);
	}

	@Override
	public float getFloat(String path, Float defaultValue) {
		return this.configSection.getFloat(path, defaultValue);
	}

	@Override
	public int getInt(String path) {
		return this.configSection.getInt(path);
	}

	@Override
	public int getInt(String path, Integer defaultValue) {
		return this.configSection.getInt(path, defaultValue);
	}

	@Override
	public List<Integer> getIntegerList(String path) {
		return this.configSection.getIntegerList(path);
	}

	@Override
	public List<Integer> getIntegerList(String path, List<Integer> defaultValue) {
		return this.configSection.getIntegerList(path, defaultValue);
	}

	@Override
	public long getLong(String path) {
		return this.configSection.getLong(path);
	}

	@Override
	public long getLong(String path, Long defaultValue) {
		return this.configSection.getLong(path, defaultValue);
	}

	@Override
	public List<Number> getNumberList(String path) {
		return this.configSection.getNumberList(path);
	}

	@Override
	public List<Number> getNumberList(String path, List<Number> defaultValue) {
		return this.configSection.getNumberList(path, defaultValue);
	}

	@Override
	public short getShort(String path) {
		return this.configSection.getShort(path);
	}

	@Override
	public short getShort(String path, Short defaultValue) {
		return this.configSection.getShort(path, defaultValue);
	}

	@Override
	public String getString(String path) {
		return this.configSection.getString(path);
	}

	@Override
	public String getString(String path, String defaultValue) {
		return this.configSection.getString(path, defaultValue);
	}

	@Override
	public List<String> getStringList(String path) {
		return this.configSection.getStringList(path);
	}

	@Override
	public List<String> getStringList(String path, List<String> defaultValue) {
		return this.configSection.getStringList(path, defaultValue);
	}

	@Override
	public Map<String, Object> getValues() {
		return this.configSection.getValues();
	}

	@Override
	public Set<String> keySet() {
		return this.configSection.keySet();
	}

	protected void saveFile(File destinationFile) throws IOException {
		if (!destinationFile.exists()) {
			if (!destinationFile.getAbsoluteFile().getParentFile().exists()) {
				destinationFile.getAbsoluteFile().getParentFile().mkdirs();
			}
			destinationFile.createNewFile();
		}
	}

	@Override
	public void set(String path, Object value) {
		this.configSection.set(path, value);
	}

	@Override
	public int size() {
		return this.configSection.size();
	}

}