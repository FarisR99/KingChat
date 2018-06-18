package com.faris.kingchat.core.helper;

import com.google.gson.Gson;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.*;

public class Utilities {

	public static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9]*$");

	private static Gson gson;

	private Utilities() {
	}

	public static Gson getGson() {
		if (gson == null) gson = new Gson();
		return gson;
	}

	public static String getThrowableAsString(Throwable throwable) {
		StringWriter exceptionWriter = new StringWriter();
		PrintWriter exceptionPrintWriter = new PrintWriter(exceptionWriter);
		throwable.printStackTrace(exceptionPrintWriter);
		final String exceptionAsString = exceptionWriter.toString();
		exceptionPrintWriter.close();
		return exceptionAsString;
	}

	public static boolean isInteger(String aString) {
		try {
			Integer.parseInt(aString);
			return true;
		} catch (Exception ignored) {
		}
		return false;
	}

	public static OptionalInt parseInt(String aString) {
		try {
			return OptionalInt.of(Integer.parseInt(aString));
		} catch (Exception ignored) {
			return OptionalInt.empty();
		}
	}

	public static String trimData(String dataIn) {
		StringBuilder sbMessage = new StringBuilder();
		int lastIndex = -1;
		for (int i = 0; i < dataIn.length() - 1; i++) {
			char charAt = dataIn.charAt(i);
			if (charAt == '/' && dataIn.charAt(i + 1) == 'e') {
				lastIndex = i;
			}
			sbMessage.append(charAt);
		}
		if (lastIndex != -1) return sbMessage.substring(0, lastIndex);
		else return null;
	}

}
