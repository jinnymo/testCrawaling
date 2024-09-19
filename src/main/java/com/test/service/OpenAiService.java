package com.test.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class OpenAiService {

	@Value("${api.openai.api.key}")
	private String apiKey;

	@Value("${api.openai.api.model}")
	private String modelId;

	@Autowired
	private RestTemplate restTemplate;

	public String callGptModel(String prompt) {
		String url = "https://api.openai.com/v1/chat/completions";

		// HTTP 요청 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(apiKey);

		// 요청 본문 설정
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("model", modelId); // 파인튜닝된 GPT-4 모델이 있는 경우 해당 모델 ID 사용
		List<Map<String, String>> messages = new ArrayList<>();
		Map<String, String> userMessage = new HashMap<>();
		userMessage.put("role", "user");
		userMessage.put("content", prompt);
		messages.add(userMessage);
		requestBody.put("messages", messages);
		requestBody.put("max_tokens", 300); // 필요에 따라 토큰 설정
		// 요청 엔터티 생성
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

		// API 호출 및 응답 처리
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			// OpenAI의 응답을 처리
			return response.getBody();
		} else {
			throw new RuntimeException("OpenAI API 호출 실패: " + response.getStatusCode());
		}
	}

	public JsonArray getNoticeToJsonListByGpt(JsonArray noticeJsonArray) {
		Gson gson = new Gson();
		JsonArray gptJsonArray = new JsonArray();
		
		if (!noticeJsonArray.isEmpty()) {
			for (JsonElement prompt : noticeJsonArray) {
				String promptStr = gson.toJson(prompt);
				String gptAnswerStr = callGptModel(promptStr);
				JsonObject jsonObject = JsonParser.parseString(gptAnswerStr).getAsJsonObject();

				// "choices" 배열에서 첫 번째 요소 가져오기
				JsonArray choicesArray = jsonObject.getAsJsonArray("choices");
				JsonObject firstChoice = choicesArray.get(0).getAsJsonObject();

				// "message" 객체에서 "content" 필드 가져오기
				JsonObject messageObject = firstChoice.getAsJsonObject("message");
				String content = messageObject.get("content").getAsString();
				gptJsonArray.add(JsonParser.parseString(content));
			}
		}
		
		return gptJsonArray;
	}
}
