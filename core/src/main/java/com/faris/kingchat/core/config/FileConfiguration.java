package com.faris.kingchat.core.config;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class FileConfiguration implements IConfiguration {

	public abstract Object get(String path, Object defaultValue);

	public abstract byte getByte(String path, Byte defaultValue);

	public abstract double getDouble(String path, Double defaultValue);

	public abstract float getFloat(String path, Float defaultValue);

	public abstract int getInt(String path, Integer defaultValue);

	public abstract List<Integer> getIntegerList(String path, List<Integer> defaultValue);

	public abstract long getLong(String path, Long defaultValue);

	public abstract List<Number> getNumberList(String path, List<Number> defaultValue);

	public abstract short getShort(String path, Short defaultValue);

	public abstract String getString(String path, String defaultValue);

	public abstract List<String> getStringList(String path, List<String> defaultValue);

	public abstract void load(File file) throws IOException;

	public abstract void save(File file) throws IOException;

}