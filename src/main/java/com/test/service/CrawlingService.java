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
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.test.utils.WebDriverConnect;

@Service
public class CrawlingService {

	private static WebDriver driver = WebDriverConnect.GET_WEB_DRIVER();
	public static final int NOTICE_EACH = 2;
	
	public List<Map<String, Object>> getCrawalingData() throws Exception {
		String driverPath = System.getProperty("os.name").toLowerCase().contains("win") 
	            ? "src/main/resources/driver/msedgedriver.exe"
	            : "/opt/microsoft/msedge/microsoft-edge"; // 리눅스의 경우 적절한 경로로 변경

			System.setProperty("webdriver.edge.driver", driverPath);

			Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			
			List<Map<String, Object>> jobDataList = new ArrayList<>();
			List<String> wantedJobIds = getJobIds();
			jobDataList.addAll(selectWanted(wantedJobIds));
			driver.quit();
			return jobDataList;
	}
    
	public static List<String> getJobIds() {
        List<String> jobIdList = new ArrayList<>();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            driver.get("https://www.wanted.co.kr/wdlist/518?country=kr&job_sort=job.latest_order&years=-1&locations=all");

            while (jobIdList.size() < NOTICE_EACH) {
                // 현재 페이지에서 job cards를 찾습니다.
                List<WebElement> jobCards = driver.findElements(By.cssSelector("div.JobCard_JobCard__thumb__WU1ax"));

                for (WebElement jobCard : jobCards) {
                    String positionId = jobCard.findElement(By.cssSelector("button.bookmarkBtn")).getAttribute("data-position-id");
                    String positionName = jobCard.findElement(By.cssSelector("button.bookmarkBtn")).getAttribute("data-position-name");

                    // "장애인" 또는 "보충역"이 포함된 경우 제외
                    if (!positionName.contains("장애인") && !positionName.contains("보충역")) {
                        if (!jobIdList.contains(positionId)) { // 중복 제거
                            jobIdList.add(positionId);
                        }
                    }

                    if (jobIdList.size() >= NOTICE_EACH) {
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
        } finally {
            // 드라이버 종료
            //driver.quit();
        }

        // 리스트를 배열로 변환하여 반환
        return jobIdList;
    }
	
	public static List<Map<String, Object>> selectWanted(List<String> jobIds) throws Exception {
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
	
	private static Map<String, Object> parseWantedJobDetails(Document doc, String url, Integer jobId) {
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
				? splitHtmlByBr(dutiesElement.parent().selectFirst("p span").html().trim())
				: List.of("N/A");

		Element qualificationsElement = doc.selectFirst("h3:matchesOwn(자격요건)");
		List<String> qualificationsList = qualificationsElement != null
				? splitHtmlByBr(qualificationsElement.parent().selectFirst("p span").html().trim())
				: List.of("N/A");

		Element preferredQualificationsElement = doc.selectFirst("h3:matchesOwn(우대사항)");
		List<String> preferredQualificationsList = preferredQualificationsElement != null
				? splitHtmlByBr(preferredQualificationsElement.parent().selectFirst("p span").html().trim())
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
	
	private static List<String> splitHtmlByBr(String html) {
		return List.of(html.split("<br>")).stream().map(String::trim).filter(s -> !s.isEmpty())
				.collect(Collectors.toList());
	}
	
}
