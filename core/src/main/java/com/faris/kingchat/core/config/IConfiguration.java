package com.faris.kingchat.core.config;

import java.util.*;

public interface IConfiguration {

	void addDefault(String path, Object defaultValue);

	boolean contains(String path);

	ConfigurationSection createSection(String path);

	Object get(String path);

	byte getByte(String path);

	ConfigurationSection getConfigurationSection(String path);

	double getDouble(String path);

	float getFloat(String path);

	int getInt(String path);

	List<Integer> getIntegerList(String path);

	long getLong(String path);

	List<Number> getNumberList(String path);

	short getShort(String path);

	String getString(String path);

	List<String> getStringList(String path);

	Map<String, Object> getValues();

	Set<String> keySet();

	void set(String path, Object value);

	int size();

}