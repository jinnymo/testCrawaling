package com.test.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.tenco.perfectfolio.repository.interfaces.crawling.CrawlingRepository;
import com.tenco.perfectfolio.utils.Define;
import com.tenco.perfectfolio.utils.WebDriverConnect;

@Service
public class JumpitCrawlingService {

	private WebDriver driver = WebDriverConnect.getWebDriver();
	
	/**
	 * 점핏에서 url 아이디 정보 수집
	 * @return url 아이디를 담은 List
	 */
	public List<String> getJumpitJobIds(List<Integer> noticeIdList) {
		List<String> jobIdList = new ArrayList<>();
		JavascriptExecutor js = (JavascriptExecutor) driver;

			driver.get("https://www.jumpit.co.kr/positions?sort=rsp_rate");

			while (jobIdList.size() < Define.NOTICE_EACH) {
				List<WebElement> jobCards = driver.findElements(By.cssSelector("div.sc-d609d44f-0.grDLmW > a"));

				for (WebElement jobCard : jobCards) {
					String href = jobCard.getAttribute("href");

					if (href != null && href.contains("/position/")) {
						String jobId = href.substring(href.lastIndexOf("/position/") + 10);
						try {
							if (!jobIdList.contains(jobId) && !noticeIdList.contains(Integer.parseInt(jobId))) { // 중복 제거
								jobIdList.add(jobId);
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
							continue;
						}
						if (jobIdList.size() >= Define.NOTICE_EACH) {
							break;
						}
					}
				}

				// 스크롤을 페이지 하단으로 이동
				js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

				// 새로운 데이터가 로드될 시간을 줍니다.
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		return jobIdList;
	}
	
	/**
	 * 점핏에서 수집된 url 아이디 List로 각 공고 크롤링한 결과 반환 메소드
	 * @param jobIds
	 * @return 크롤링된 JSON 데이터 반환
	 * @throws Exception
	 */
	public List<Map<String, Object>> getJumpitNotice(List<String> jobIds) throws Exception {
		List<Map<String, Object>> jobDataList = new ArrayList<>();

			for (String jobId : jobIds) {
				String url = "https://www.jumpit.co.kr/position/" + jobId;
				driver.get(url);

				// Wait for the page to load
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
				wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));

				Document doc = Jsoup.parse(driver.getPageSource());

				Map<String, Object> jobData = parseJumpitJobDetails(doc, url, Integer.parseInt(jobId));
				if (jobData != null) {
					jobDataList.add(jobData);
				}
			}
		return jobDataList;
	}
	
	/**
	 * 점핏 공고에서 크롤링하는 메소드
	 * @param doc
	 * @param url
	 * @param jobId
	 * @return
	 */
	private Map<String, Object> parseJumpitJobDetails(Document doc, String url, Integer jobId) {
		Map<String, Object> jobData = new LinkedHashMap<>();

		String mainText = doc.selectFirst("div.sc-f491c6ef-0 h1").text().trim();
		if (mainText.contains("병역특례")) {
			return null;
		}

		String companyName = doc.selectFirst("a.name").text().trim();
		String experience = doc.selectFirst("dl.sc-b12ae455-1:contains(경력) dd").text().trim();
		String endDate = doc.selectFirst("dl.sc-b12ae455-1:contains(마감일) dd").text().trim();
		List<String> techStackList = doc.selectFirst("dl.sc-e76d2562-0 dd").select("div.sc-d9de2de1-0").stream()
				.map(Element::text).collect(Collectors.toList());
		List<String> workInfo = doc.select("dl.sc-e76d2562-0").get(1).select("dd pre").text().lines()
				.filter(s -> !s.trim().isEmpty()).collect(Collectors.toList());
		List<String> qualifications = doc.select("dl.sc-e76d2562-0").get(2).select("dd pre").text().lines()
				.filter(s -> !s.trim().isEmpty()).collect(Collectors.toList());
		List<String> preferred = doc.select("dl.sc-e76d2562-0").get(3).select("dd pre").text().lines()
				.filter(s -> !s.trim().isEmpty()).collect(Collectors.toList());

		jobData.put("id", jobId);
		jobData.put("title", mainText);
		jobData.put("job_url", url);
		jobData.put("company_name", companyName);
		jobData.put("experience", experience);
		jobData.put("qualifications", qualifications);
		jobData.put("work_info", workInfo);
		jobData.put("preferred", preferred);
		jobData.put("skills", techStackList);
		jobData.put("end_date", endDate);
		jobData.put("site", "jumpit");

		return jobData;
	}
	
}
