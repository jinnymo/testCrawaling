package com.test.controller;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.test.repository.entity.NoticeEntity;
import com.test.service.CrawlingService;
import com.test.service.NoticeModifyService;
import com.test.service.OpenAiService;

@Controller
@RequestMapping("/crawl")
public class CrawlingController {

	@Autowired
	private CrawlingService crawlingService;
	@Autowired
	private OpenAiService openAiService;
	@Autowired
	private NoticeModifyService jsonNoticeService;

	private int currentIndex = 0;

	@GetMapping("/start")
	public String crawlWanted(Model model) throws Exception {
		List<Map<String, Object>> jobDataList = crawlingService.getCrawlingData();
		JsonArray noticeJsonArray = crawlingService.getNoticeToJsonList(jobDataList);
		
		// TODO 크롤링 원본 데이터가 실제 저장됨.
		crawlingService.insertOriginNoticeJson(noticeJsonArray);

		// TODO GPT API 호출되어 데이터가 저장됨.
//		JsonArray promptJsonArray = crawlingService.getNoticeToJsonListByGptPrompt(jobDataList);
//		JsonArray gptJsonArray = openAiService.getNoticeToJsonListByGpt(promptJsonArray);
//
//		crawlingService.insertNoticeJsonByGpt(promptJsonArray, gptJsonArray);

		model.addAttribute("jobDataList", jobDataList);
		return "testCrawlResult"; // JSP 파일명
	}

	@GetMapping("/editPage")
	public String editNoticePage(Model model) {
	    // 현재 처리할 첫 번째 NoticeEntity 가져오기
	    List<NoticeEntity> notices = jsonNoticeService.getIncompleteNotices();
	    if (notices.isEmpty()) {
	        model.addAttribute("message", "불러올 데이터가 없습니다.");
	        return "editNotice";
	    }
	    
	    NoticeEntity notice = notices.get(currentIndex); // 첫 번째 NoticeEntity 가져오기
	    JsonObject gptJson = jsonNoticeService.getGptJsonAsObject(notice.getId()); // 해당 notice의 GPT JSON 가져오기

	    // JsonObject를 Map으로 변환
	    Gson gson = new Gson();
	    Type type = new TypeToken<Map<String, Object>>(){}.getType();
	    Map<String, Object> gptJsonMap = gson.fromJson(gptJson.toString(), type);

	    // 데이터를 JSP로 전달
	    model.addAttribute("notice", notice);
	    model.addAttribute("gptJson", gptJsonMap);

	    return "editNotice"; // JSP 페이지로 이동
	}

	// JSON 데이터를 받아서 업데이트하는 메서드
	@PostMapping("/edit/json")
	public ResponseEntity<String> updateNoticeWithJson(@RequestBody Map<String, Object> payload) {
	    Long id = Long.valueOf(payload.get("id").toString());
	    String gptJson = payload.get("gpt_json").toString();

	    JsonObject updatedGptJson = JsonParser.parseString(gptJson).getAsJsonObject();
	    jsonNoticeService.updateGptJson(id, updatedGptJson);
	    
	    // 성공적으로 처리되었음을 알리기 위해 HTTP 200 응답 반환
	    return ResponseEntity.ok("Data updated successfully");
	}


	// 다음 데이터로 이동
	@GetMapping("/next")
	public String nextNotice() {
		currentIndex++;
		return "redirect:/notices/edit";
	}

}
