package com.test.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Json {

    public static String jsonToBeauty(String data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
        String prettyJson = gson.toJson(jsonObject);
        return prettyJson;
    }

}
