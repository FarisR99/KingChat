package com.faris.kingchat.core.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class ConfigurationSection implements IConfiguration {

	private JsonNode configMap = null;

	protected ConfigurationSection(JsonNode node) {
		this.configMap = node;
	}

	@Override
	public void addDefault(String path, Object value) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (!this.contains(path)) this.set(path, value);
	}

	@Override
	public boolean contains(String path) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			return section != null && section.contains(newPath);
		}
		return this.configMap.has(path);
	}

	@Override
	public ConfigurationSection createSection(String path) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			if (section == null) section = this.createSection(sectionPath);
			return section.createSection(newPath);
		}
		return new ConfigurationSection(this.getObjectNode().putObject(path));
	}

	@Override
	public Object get(String path) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		return this.get(path, null);
	}

	public Object get(String path, Object defaultValue) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			return section != null ? section.get(newPath, defaultValue) : defaultValue;
		}
		return this.contains(path) ? toObject(this.getObjectNode().get(path)) : defaultValue;
	}

	@Override
	public byte getByte(String path) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		return this.getByte(path, null);
	}

	public byte getByte(String path, Byte defaultValue) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			return section != null ? section.getByte(newPath, defaultValue) : defaultValue;
		}
		return this.contains(path) ? this.getObjectNode().get(path).numberValue().byteValue() : defaultValue;
	}

	@Override
	public ConfigurationSection getConfigurationSection(String path) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			return section != null ? section.getConfigurationSection(newPath) : null;
		}
		return this.contains(path) ? new ConfigurationSection(this.getObjectNode().get(path)) : null;
	}

	@Override
	public double getDouble(String path) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		return this.getDouble(path, null);
	}

	public double getDouble(String path, Double defaultValue) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			return section != null ? section.getDouble(newPath, defaultValue) : defaultValue;
		}
		return this.contains(path) ? this.getObjectNode().get(path).asDouble(defaultValue != null ? defaultValue : 0D) : defaultValue;
	}

	@Override
	public float getFloat(String path) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		return this.getInt(path, null);
	}

	public float getFloat(String path, Float defaultValue) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			return section != null ? section.getFloat(newPath, defaultValue) : defaultValue;
		}
		return this.contains(path) ? this.getObjectNode().get(path).numberValue().floatValue() : defaultValue;
	}

	@Override
	public int getInt(String path) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		return this.getInt(path, null);
	}

	public int getInt(String path, Integer defaultValue) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			return section != null ? section.getInt(newPath, defaultValue) : defaultValue;
		}
		return this.contains(path) ? this.getObjectNode().get(path).asInt(defaultValue != null ? defaultValue : 0) : defaultValue;
	}

	@Override
	public List<Integer> getIntegerList(String path) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		return this.getIntegerList(path, new ArrayList<>());
	}

	public List<Integer> getIntegerList(String path, List<Integer> defaultValue) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			return section != null ? section.getIntegerList(newPath, defaultValue) : defaultValue;
		}
		if (!this.contains(path)) return defaultValue;
		List<Integer> intList = new ArrayList<>();
		JsonNode jsonNode = this.getObjectNode().get(path);
		if (jsonNode.isArray()) {
			for (JsonNode objNode : jsonNode) intList.add(objNode.asInt());
		}
		return intList;
	}

	@Override
	public long getLong(String path) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		return this.getLong(path, null);
	}

	public long getLong(String path, Long defaultValue) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			return section != null ? section.getLong(newPath, defaultValue) : defaultValue;
		}
		return this.contains(path) ? this.getObjectNode().get(path).asLong(defaultValue != null ? defaultValue : 0L) : defaultValue;
	}

	@Override
	public List<Number> getNumberList(String path) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		return this.getNumberList(path, new ArrayList<>());
	}

	public List<Number> getNumberList(String path, List<Number> defaultValue) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			return section != null ? section.getNumberList(newPath, defaultValue) : defaultValue;
		}
		if (!this.contains(path)) return defaultValue;
		List<Number> numList = new ArrayList<>();
		JsonNode jsonNode = this.getObjectNode().get(path);
		if (jsonNode.isArray()) {
			for (JsonNode objNode : jsonNode) {
				numList.add((Number) toObject(objNode));
			}
		}
		return numList;
	}

	protected ObjectNode getObjectNode() {
		return (ObjectNode) this.configMap;
	}

	@Override
	public short getShort(String path) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		return this.getShort(path, null);
	}

	public short getShort(String path, Short defaultValue) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			return section != null ? section.getShort(newPath, defaultValue) : defaultValue;
		}
		return this.contains(path) ? this.getObjectNode().get(path).shortValue() : defaultValue;
	}

	@Override
	public String getString(String path) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		return this.getString(path, null);
	}

	public String getString(String path, String defaultValue) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			return section != null ? section.getString(newPath, defaultValue) : defaultValue;
		}
		return this.contains(path) ? this.getObjectNode().get(path).asText(defaultValue) : defaultValue;
	}

	@Override
	public List<String> getStringList(String path) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		return this.getStringList(path, new ArrayList<>());
	}

	public List<String> getStringList(String path, List<String> defaultValue) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			return section != null ? section.getStringList(newPath, defaultValue) : defaultValue;
		}
		if (!this.contains(path)) return defaultValue;
		List<String> stringList = new ArrayList<>();
		JsonNode jsonNode = this.getObjectNode().get(path);
		if (jsonNode.isArray()) {
			for (JsonNode objNode : jsonNode) stringList.add(objNode.asText());
		}
		return stringList;
	}

	@Override
	public Map<String, Object> getValues() {
		Map<String, Object> values = new LinkedHashMap<>();
		Iterator<Map.Entry<String, JsonNode>> fieldsIterator = this.configMap.fields();
		fieldsIterator.forEachRemaining(fieldEntry -> {
			JsonNode jsonValue = fieldEntry.getValue();
			if (!jsonValue.isNull()) {
				String path = fieldEntry.getKey();
				Object value = toObject(jsonValue);
				if (value != null) values.put(path, value);
			}
		});
		return values;
	}

	@Override
	public Set<String> keySet() {
		Iterator<String> fieldNamesIterator = this.getObjectNode().fieldNames();
		Set<String> keySet = new LinkedHashSet<>();
		fieldNamesIterator.forEachRemaining(keySet::add);
		return keySet;
	}

	@Override
	public void set(String path, Object value) {
		if (path == null) throw new NullPointerException("The path cannot be null!");
		if (path.contains(".")) {
			String sectionPath = path.substring(0, path.indexOf('.'));
			String newPath = path.substring(sectionPath.length() + 1);
			ConfigurationSection section = this.getConfigurationSection(sectionPath);
			if (section != null) {
				section.set(newPath, value);
			} else {
				if (value != null) {
					section = this.createSection(sectionPath);
					section.set(newPath, value);
				}
			}
			return;
		}
		if (value != null) {
			if (value instanceof Boolean) this.getObjectNode().put(path, (Boolean) value);
			else if (value instanceof Byte) this.getObjectNode().put(path, (Byte) value);
			else if (value instanceof Double) this.getObjectNode().put(path, (Double) value);
			else if (value instanceof Float) this.getObjectNode().put(path, (Float) value);
			else if (value instanceof Integer) this.getObjectNode().put(path, (Integer) value);
			else if (value instanceof Long) this.getObjectNode().put(path, (Long) value);
			else if (value instanceof Short) this.getObjectNode().put(path, (Short) value);
			else if (value instanceof List) {
				ArrayNode arrayNode = this.getObjectNode().putArray(path);
				List<?> rawList = (List<?>) value;
				for (Object rawObject : rawList) {
					if (rawObject != null) {
						if (rawObject instanceof Boolean) arrayNode.add((Boolean) rawObject);
						else if (rawObject instanceof Byte) arrayNode.add((Byte) rawObject);
						else if (rawObject instanceof Double) arrayNode.add((Double) rawObject);
						else if (rawObject instanceof Float) arrayNode.add((Float) rawObject);
						else if (rawObject instanceof Integer) arrayNode.add((Integer) rawObject);
						else if (rawObject instanceof Long) arrayNode.add((Long) rawObject);
						else if (rawObject instanceof Short) arrayNode.add((Short) rawObject);
						else if (rawObject instanceof BigInteger) arrayNode.add((BigInteger) rawObject);
						else if (rawObject instanceof BigDecimal) arrayNode.add((BigDecimal) rawObject);
						else if (rawObject instanceof byte[]) arrayNode.add((byte[]) rawObject);
						else if (rawObject instanceof List) listToArrayNode(arrayNode, (List<?>) rawObject);
						else arrayNode.add(rawObject.toString());
					}
				}
			} else if (value instanceof Map) {
				ConfigurationSection newSection = new ConfigurationSection(this.getObjectNode().putObject(path));
				Map<?, ?> rawMap = (Map<?, ?>) value;
				for (Map.Entry<?, ?> rawEntry : rawMap.entrySet()) {
					if (rawEntry.getKey() != null) {
						String key = rawEntry.getKey().toString();
						newSection.set(key, rawEntry.getValue());
					}
				}
			} else if (value instanceof JsonSerializable) {
				this.getObjectNode().putRawValue(path, new RawValue((JsonSerializable) value));
			} else {
				this.getObjectNode().put(path, value.toString());
			}
		} else {
			if (this.contains(path)) this.getObjectNode().remove(path);
		}
	}

	@Override
	public int size() {
		return this.configMap.size();
	}

	private static ArrayNode listToArrayNode(ArrayNode parentNode, List<?> rawList) {
		ArrayNode arrayNode = parentNode.addArray();
		for (Object rawObject : rawList) {
			if (rawObject != null) {
				if (rawObject instanceof Boolean) arrayNode.add((Boolean) rawObject);
				else if (rawObject instanceof Byte) arrayNode.add((Byte) rawObject);
				else if (rawObject instanceof Double) arrayNode.add((Double) rawObject);
				else if (rawObject instanceof Float) arrayNode.add((Float) rawObject);
				else if (rawObject instanceof Integer) arrayNode.add((Integer) rawObject);
				else if (rawObject instanceof Long) arrayNode.add((Long) rawObject);
				else if (rawObject instanceof Short) arrayNode.add((Short) rawObject);
				else if (rawObject instanceof BigInteger) arrayNode.add((BigInteger) rawObject);
				else if (rawObject instanceof BigDecimal) arrayNode.add((BigDecimal) rawObject);
				else if (rawObject instanceof byte[]) arrayNode.add((byte[]) rawObject);
				else if (rawObject instanceof List) listToArrayNode(arrayNode, (List<?>) rawObject);
				else arrayNode.add(rawObject.toString());
			}
		}
		return arrayNode;
	}

	private static Object toObject(JsonNode node) {
		if (node == null || node.isNull()) return null;
		Object value = null;
		if (node.isBoolean()) {
			value = node.booleanValue();
		} else if (node.isDouble()) {
			value = node.doubleValue();
		} else if (node.isFloat()) {
			value = node.floatValue();
		} else if (node.isInt()) {
			value = node.intValue();
		} else if (node.isLong()) {
			value = node.longValue();
		} else if (node.isShort()) {
			value = node.shortValue();
		} else if (node.isTextual()) {
			value = node.textValue();
		} else if (node.isBigInteger()) {
			value = node.bigIntegerValue();
		} else if (node.isBigDecimal()) {
			value = node.decimalValue();
		} else if (node.isNumber()) {
			value = node.numberValue();
		} else if (node.isArray()) {
			List<Object> list = new ArrayList<>();
			for (JsonNode listEntry : node) {
				Object entryValue = toObject(listEntry);
				if (entryValue != null) list.add(entryValue);
			}
			value = list;
		} else if (node.isObject()) {
			Map<String, Object> map = new LinkedHashMap<>();
			node.fields().forEachRemaining(entry -> {
				Object entryValue = toObject(entry.getValue());
				if (entryValue != null) map.put(entry.getKey(), entryValue);
			});
			value = map;
		}
		return value;
	}

}