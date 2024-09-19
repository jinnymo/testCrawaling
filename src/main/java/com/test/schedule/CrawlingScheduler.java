package com.test.schedule;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.test.service.CrawlingService;
import com.test.service.OpenAiService;

@Component
public class CrawlingScheduler {

	@Autowired
    private CrawlingService crawlingService;
	@Autowired
	private OpenAiService openAiService;

	@Scheduled(cron = "0 0 0 * * *")
    public void crawlWanted() throws Exception {
		List<Map<String, Object>> jobDataList = crawlingService.getCrawlingData();
		JsonArray noticeJsonArray = crawlingService.getNoticeToJsonList(jobDataList);
		
		// TODO 크롤링 원본 데이터가 실제 저장됨.
		crawlingService.insertOriginNoticeJson(noticeJsonArray);

		// TODO GPT API 호출되어 데이터가 저장됨.
//		JsonArray promptJsonArray = crawlingService.getNoticeToJsonListByGptPrompt(jobDataList);
//		JsonArray gptJsonArray = openAiService.getNoticeToJsonListByGpt(promptJsonArray);
//		crawlingService.insertNoticeJsonByGpt(promptJsonArray, gptJsonArray);
		
	    System.out.println("스케쥴러 실행");
	    // 로깅 및 예외 처리 필요
    }
}

