package com.test.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtil {

	private static final Gson gson = new Gson();
	
    /**
     * JsonElement에서 key가 "Id"인 값을 반환하는 메서드
     * @param jsonElement JsonElement 객체
     * @return key가 "Id"인 값, 없으면 null
     */
    public static Integer getIdValue(JsonElement jsonElement) {
        // jsonElement가 JsonObject인지 확인
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            // JsonObject에서 "Id" key를 찾아 반환
            if (jsonObject.has("id")) {
                return jsonObject.get("id").getAsInt();
            }
        }
        return null; // "Id" key가 없거나 JsonElement가 JsonObject가 아닌 경우
    }
    

    // String을 JSON으로 변환
    public static JsonObject parseJson(String jsonString) {
        return JsonParser.parseString(jsonString).getAsJsonObject();
    }

    // JSON을 String으로 변환
    public static String toJsonString(JsonObject jsonObject) {
        return gson.toJson(jsonObject);
    }
}