package com.test.utils;

import java.util.List;
import java.util.stream.Collectors;

public class StrUtil {

	public static String escapeJsonString(String jsonString) {
		return jsonString.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")
				.replace("\t", "\\t");
	}
	
	public static List<String> splitHtmlByBr(String html) {
		return List.of(html.split("<br>")).stream().map(String::trim).filter(s -> !s.isEmpty())
				.collect(Collectors.toList());
	}

}
