package com.test.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.test.service.CrawlingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/crawl")
@RequiredArgsConstructor
public class CrawlingController {

	@Autowired
    private final CrawlingService jobCrawlingService;
	
	@GetMapping("/test")
	public String getTest() {
		return "test";
	}
	

    @GetMapping("/wanted")
    public String crawlWanted(Model model) throws Exception {
        List<Map<String, Object>> jobDataList = jobCrawlingService.getCrawalingData();
        model.addAttribute("jobDataList", jobDataList);
        System.out.println(jobDataList.toString());
        return "testCrawlResult";  // JSP 파일명
    }
}

