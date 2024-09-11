package com.test.utils;

public class StrUtil {

	public static String escapeJsonString(String jsonString) {
		return jsonString.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")
				.replace("\t", "\\t");
	}

}
