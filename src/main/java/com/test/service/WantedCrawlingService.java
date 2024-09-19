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
import com.tenco.perfectfolio.utils.StrUtil;
import com.tenco.perfectfolio.utils.WebDriverConnect;

@Service
public class WantedCrawlingService {

	private WebDriver driver = WebDriverConnect.getWebDriver();
	
	/**
	 * 원티드에서 url 아이디 정보 수집
	 * @return url 아이디를 담은 List
	 */
	public List<String> getWantedJobIds(List<Integer> noticeIdList) {
        List<String> jobIdList = new ArrayList<>();
        JavascriptExecutor js = (JavascriptExecutor) driver;

            driver.get("https://www.wanted.co.kr/wdlist/518?country=kr&job_sort=job.latest_order&years=-1&locations=all");

            while (jobIdList.size() < Define.NOTICE_EACH) {
                // 현재 페이지에서 job cards를 찾습니다.
                List<WebElement> jobCards = driver.findElements(By.cssSelector("div.JobCard_JobCard__thumb__WU1ax"));

                for (WebElement jobCard : jobCards) {
                    String positionId = jobCard.findElement(By.cssSelector("button.bookmarkBtn")).getAttribute("data-position-id");
                    String positionName = jobCard.findElement(By.cssSelector("button.bookmarkBtn")).getAttribute("data-position-name");

                    // "장애인" 또는 "보충역"이 포함된 경우 제외
                    if (!positionName.contains("장애인") && !positionName.contains("보충역")) {
                    	
						// DB에 없는 공고만 id 수집
                    	try {
                    		if (!jobIdList.contains(positionId) && !noticeIdList.contains(Integer.parseInt(positionId))) { // 중복 제거
                    			jobIdList.add(positionId);
                    		}
						} catch (NumberFormatException e) {
							e.printStackTrace();
							continue;
						}
                    }

                    if (jobIdList.size() >= Define.NOTICE_EACH) {
                        break;
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

        // 리스트를 배열로 변환하여 반환
        return jobIdList;
    }
	
	/**
	 * 원티드에서 수집된 url 아이디 List로 각 공고 크롤링한 결과 반환 메소드
	 * @param jobIds
	 * @return 크롤링된 JSON 데이터 반환
	 * @throws Exception
	 */
	public List<Map<String, Object>> getWantedNotice(List<String> jobIds) throws Exception {
		List<Map<String, Object>> jobDataList = new ArrayList<>();
		
			for (String jobId : jobIds) {
				String url = "https://www.wanted.co.kr/wd/" + jobId;
				driver.get(url);

				// Wait for the page to load
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
				wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));

				// Click "상세 정보 더 보기" button if available
				try {
					WebElement button = wait.until(ExpectedConditions
							.visibilityOfElementLocated(By.xpath("//button[./span[text()='상세 정보 더 보기']]")));
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
					wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h3[contains(text(), '우대사항')]")));
				} catch (Exception e) {
					System.out.println("상세 정보 더 보기 버튼을 찾을 수 없거나 클릭할 수 없음: " + e.getMessage());
					continue; // Skip to the next jobId
				}

				Document doc = Jsoup.parse(driver.getPageSource());

				Map<String, Object> jobData = parseWantedJobDetails(doc, url, Integer.parseInt(jobId));
				jobDataList.add(jobData);
			}

		return jobDataList;
	}
	
	/**
	 * 원티드 공고에서 크롤링하는 메소드
	 * @param doc
	 * @param url
	 * @param jobId
	 * @return
	 */
	private Map<String, Object> parseWantedJobDetails(Document doc, String url, Integer jobId) {
		Map<String, Object> jobData = new LinkedHashMap<>();

		Element mainTextElement = doc.selectFirst("h1[class*=JobHeader_JobHeader__PositionName__]");
		String mainText = mainTextElement != null ? mainTextElement.text().trim() : "N/A";

		Element companyNameElement = doc.selectFirst("a[class*=JobHeader_JobHeader__Tools__Company__Link__]");
		String companyName = companyNameElement != null ? companyNameElement.text().trim() : "N/A";

		Element experienceElement = doc
				.selectFirst("span.JobHeader_JobHeader__Tools__Company__Info__yT4OD:matches(.*경력.*|.*신입.*)");
		String experience = experienceElement != null ? experienceElement.text().trim() : "N/A";

		Element dutiesElement = doc.selectFirst("h3:contains(주요업무)");
		List<String> dutiesList = dutiesElement != null
				? StrUtil.splitHtmlByBr(dutiesElement.parent().selectFirst("p span").html().trim())
				: List.of("N/A");

		Element qualificationsElement = doc.selectFirst("h3:matchesOwn(자격요건)");
		List<String> qualificationsList = qualificationsElement != null
				? StrUtil.splitHtmlByBr(qualificationsElement.parent().selectFirst("p span").html().trim())
				: List.of("N/A");

		Element preferredQualificationsElement = doc.selectFirst("h3:matchesOwn(우대사항)");
		List<String> preferredQualificationsList = preferredQualificationsElement != null
				? StrUtil.splitHtmlByBr(preferredQualificationsElement.parent().selectFirst("p span").html().trim())
				: List.of("N/A");

		Element skillTagsElement = doc.selectFirst("ul.JobSkillTags_JobSkillTags__list__01GRk");
		List<String> skillTagsList = skillTagsElement != null
				? skillTagsElement.select("li.SkillTagItem_SkillTagItem__K3B3t span").stream().map(Element::text)
						.collect(Collectors.toList())
				: List.of("N/A");

		Element endDateElement = doc.selectFirst("h2:contains(마감일)");
		String endDate = endDateElement != null ? endDateElement.nextElementSibling().text().trim() : "N/A";

		jobData.put("id", jobId);
		jobData.put("title", mainText);
		jobData.put("job_url", url);
		jobData.put("company_name", companyName);
		jobData.put("experience", experience);
		jobData.put("qualifications", qualificationsList);
		jobData.put("work_info", dutiesList);
		jobData.put("preferred", preferredQualificationsList);
		jobData.put("skills", skillTagsList);
		jobData.put("end_date", endDate);
		jobData.put("site", "wanted");

		return jobData;
	}
	
}
