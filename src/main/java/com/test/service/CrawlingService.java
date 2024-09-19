package com.test.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tenco.perfectfolio.dto.admin.CategoryDTO;
import com.tenco.perfectfolio.repository.model.crawl.JsonNoticeSkill;
import com.tenco.perfectfolio.service.DataService;
import com.tenco.perfectfolio.utils.Define;
import com.test.repository.CrawlingRepository;
import com.test.utils.WebDriverConnect;

import io.grpc.internal.JsonUtil;

@Service
public class CrawlingService {

	@Autowired
	private WantedCrawlingService wantedCrawlingService;
	@Autowired
	private JumpitCrawlingService jumpitCrawlingService;
	@Autowired
	private DataService dataService;

	@Autowired
	private CrawlingRepository crawlingRepository;

	public List<Map<String, Object>> getCrawlingData() throws Exception {

		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

		List<Integer> noticeIdList = crawlingRepository.getNoticeIdList();

		List<Map<String, Object>> jobDataList = new ArrayList<>();
		List<String> wantedJobIds = wantedCrawlingService.getWantedJobIds(noticeIdList);
		List<String> jumpitJobIds = jumpitCrawlingService.getJumpitJobIds(noticeIdList);
		jobDataList.addAll(wantedCrawlingService.getWantedNotice(wantedJobIds));
		jobDataList.addAll(jumpitCrawlingService.getJumpitNotice(jumpitJobIds));
		WebDriverConnect.quitDriver();
		return jobDataList;
	}

	public JsonArray getNoticeToJsonList(List<Map<String, Object>> crawlingData) {
		JsonArray jsonArray = new JsonArray();
		Gson gson = new Gson();
		for (Map<String, Object> jobData : crawlingData) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id", (int) jobData.get("id"));
			jsonObject.addProperty("title", (String) jobData.get("title"));
			jsonObject.addProperty("job_url", (String) jobData.get("job_url"));
			jsonObject.addProperty("company_name", (String) jobData.get("company_name"));
			jsonObject.addProperty("experience", (String) jobData.get("experience"));
			jsonObject.add("qualifications", gson.toJsonTree(jobData.get("qualifications")));
			jsonObject.add("work_info", gson.toJsonTree(jobData.get("work_info")));
			jsonObject.add("preferred", gson.toJsonTree(jobData.get("preferred")));

			// 제네릭 경로를 무시하는 어노테이션
			@SuppressWarnings("unchecked")
			List<String> skills = (List<String>) jobData.get("skills");
			if (skills != null) {
				// Immutable list인 경우, 수정 가능한 리스트로 변환
				skills = new ArrayList<>(skills);

				// "N/A" 항목이 있는 경우 이를 제거
				skills.removeIf(skill -> "N/A".equalsIgnoreCase(skill));

				// 빈 배열이 아닌 경우에만 추가
				jsonObject.add("skills", gson.toJsonTree(skills));
			}

			jsonObject.addProperty("end_date", (String) jobData.get("end_date"));
			jsonObject.addProperty("site", (String) jobData.get("site"));

			jsonArray.add(jsonObject);
		}
		;
		return jsonArray;
	}

	public JsonArray getNoticeToJsonListByGptPrompt(List<Map<String, Object>> crawlingData) {
		JsonArray jsonArray = new JsonArray();
		Gson gson = new Gson();
		for (Map<String, Object> jobData : crawlingData) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id", (int) jobData.get("id"));
			jsonObject.addProperty("title", (String) jobData.get("title"));
			jsonObject.add("qualifications", gson.toJsonTree(jobData.get("qualifications")));
			jsonObject.add("work_info", gson.toJsonTree(jobData.get("work_info")));
			jsonObject.add("preferred", gson.toJsonTree(jobData.get("preferred")));

			// 제네릭 타입 경고를 무시할 때 사용
			@SuppressWarnings("unchecked")
			List<String> skills = (List<String>) jobData.get("skills");
			// skills가 "N/A"가 아닌 경우에만 추가
			if (skills != null && !skills.contains("N/A")) {
				jsonObject.add("skills", gson.toJsonTree(skills));
			}

			jsonArray.add(jsonObject);
		}
		;
		return jsonArray;
	}

	/**
	 * JsonArray 타입 Mongo DB에 들어가는 데이터로 변환
	 * 
	 * @param inputArrays
	 * @return
	 */
	public JsonArray convertMongoDataArray(JsonArray gptJsonArray) {

		// Gson 객체
		Gson gson = new Gson();

		JsonArray outputArray = new JsonArray();

		// 입력된 JsonArray의 각 요소를 처리
		for (JsonElement element : gptJsonArray) {
			JsonObject originalObject = element.getAsJsonObject();
			JsonObject newObject = new JsonObject();

			// id 복사
			newObject.add("id", originalObject.get("id"));

			// qualifications_skill 복사 및 변환
			JsonObject qualificationsSkill = originalObject.getAsJsonObject("qualifications_skill");
			JsonObject newQualificationsSkill = new JsonObject();

			// 각 스킬 카테고리별로 처리
			for (Map.Entry<String, JsonElement> entry : qualificationsSkill.entrySet()) {
				String category = entry.getKey();
				JsonArray skillsArray = entry.getValue().getAsJsonArray();
				JsonObject skillsObject = new JsonObject(); // key-value 구조로 저장하기 위한 JsonObject

				// 스킬과 레벨을 [ "스킬명": 레벨 ] 형태로 변환
				for (JsonElement skillElement : skillsArray) {
					String[] skillAndLevel = skillElement.getAsString().split("/");
					String skillName = skillAndLevel[0];
					int skillLevel = Integer.parseInt(skillAndLevel[1]);

					// 변환된 형식으로 JsonObject에 추가
					skillsObject.addProperty(skillName, skillLevel);
				}

				// 새로운 qualifications_skill에 변환된 객체 추가
				newQualificationsSkill.add(category, skillsObject);
			}

			// 새로 만든 qualifications_skill을 최종 객체에 추가
			newObject.add("qualifications_skill", newQualificationsSkill);

			// 최종 변환된 객체를 outputArray에 추가
			outputArray.add(newObject);
		}

		return outputArray;
	}

	/**
	 * JsonObject 타입 Mongo DB에 들어가는 데이터로 변환 후 TODO 새로운 스킬 추가
	 * 
	 * @param inputArrays
	 * @return
	 */
	public JsonObject convertMongoDataObject(JsonObject gptJsonElement) {

		JsonObject outputObject = new JsonObject();
		
		Map<String, List<CategoryDTO>> skillCategorys = dataService.getSkillAllCategory();

		// 카테고리 리스트 가져오기
		List<CategoryDTO> languageList = skillCategorys.get("languageList");
		List<CategoryDTO> frameworkList = skillCategorys.get("frameworkList");
		List<CategoryDTO> SQLList = skillCategorys.get("SQLList");
		List<CategoryDTO> NoSQLList = skillCategorys.get("NoSQLList");
		List<CategoryDTO> devOpsList = skillCategorys.get("devOpsList");
		List<CategoryDTO> serviceList = skillCategorys.get("serviceList");

		// 임시 스킬 이름 리스트
		List<String> tempSkillList = new ArrayList<>();
		
		// 없는 스킬만 추가
		List<CategoryDTO> addNewSkill = new ArrayList<>();

		// gptJsonElement에서 qualifications_skill을 가져옴
		JsonObject qualificationsSkill = gptJsonElement.getAsJsonObject("qualifications_skill");
		JsonObject newQualificationsSkill = new JsonObject();

		// 각 스킬 카테고리별로 처리
		for (Map.Entry<String, JsonElement> entry : qualificationsSkill.entrySet()) {
			String category = entry.getKey();
			String skillsString = entry.getValue().getAsString(); // 배열처럼 되어 있는 문자열

			JsonObject skillsObject = new JsonObject(); // key-value 구조로 저장하기 위한 JsonObject

			// 문자열에서 [ ] 를 제거하고 ,로 구분하여 스킬과 레벨을 분리
			skillsString = skillsString.replace("[", "").replace("]", "");
			if (!skillsString.isEmpty()) {
				String[] skillsArray = skillsString.split(", ");

				// 각 스킬과 레벨을 [ "스킬명": 레벨 ] 형태로 변환
				for (String skillAndLevel : skillsArray) {
					String[] skillPair = skillAndLevel.split("/"); // 스킬명/레벨 형태로 분리
					String skillName = skillPair[0];
					int skillLevel = Integer.parseInt(skillPair[1]);

					// 변환된 형식으로 JsonObject에 추가
					skillsObject.addProperty(skillName, skillLevel);
					
					// 새로운 스킬이면 추가하기 TODO 메소드 사용 리팩토링
					if(Define.LANGUAGE_NAME.equalsIgnoreCase(category)) {
						for (CategoryDTO categoryDTO : languageList) {
							tempSkillList.add(categoryDTO.getName());
						}
						
						if (!tempSkillList.contains(skillName)) {
							addNewSkill.add(CategoryDTO.builder()
												.pId(Define.LANGUAGE_ID)
												.name(skillName.toUpperCase())
												.build());
						}
						tempSkillList.clear();
					} else if(Define.FRAMEWORK_NAME.equalsIgnoreCase(category)) {
						for (CategoryDTO categoryDTO : frameworkList) {
							tempSkillList.add(categoryDTO.getName());
						}
						
						if (!tempSkillList.contains(skillName)) {
							addNewSkill.add(CategoryDTO.builder()
												.pId(Define.FRAMEWORK_ID)
												.name(skillName.toUpperCase())
												.build());
						}
						tempSkillList.clear();
					} else if(Define.SQL_NAME.equalsIgnoreCase(category)) {
						for (CategoryDTO categoryDTO : SQLList) {
							tempSkillList.add(categoryDTO.getName());
						}
						
						if (!tempSkillList.contains(skillName)) {
							addNewSkill.add(CategoryDTO.builder()
												.pId(Define.SQL_ID)
												.name(skillName.toUpperCase())
												.build());
						}
						tempSkillList.clear();
					} else if(Define.NOSQL_NAME.equalsIgnoreCase(category)) {
						for (CategoryDTO categoryDTO : NoSQLList) {
							tempSkillList.add(categoryDTO.getName());
						}
						
						if (!tempSkillList.contains(skillName)) {
							addNewSkill.add(CategoryDTO.builder()
												.pId(Define.NOSQL_ID)
												.name(skillName.toUpperCase())
												.build());
						}
						tempSkillList.clear();
					} else if(Define.DEVOPS_NAME.equalsIgnoreCase(category)) {
						for (CategoryDTO categoryDTO : devOpsList) {
							tempSkillList.add(categoryDTO.getName());
						}
						
						if (!tempSkillList.contains(skillName)) {
							addNewSkill.add(CategoryDTO.builder()
												.pId(Define.DEVOPS_ID)
												.name(skillName.toUpperCase())
												.build());
						}
						tempSkillList.clear();
					} else if(Define.SERVICE_NAME.equalsIgnoreCase(category)) {
						for (CategoryDTO categoryDTO : serviceList) {
							tempSkillList.add(categoryDTO.getName());
						}
						
						if (!tempSkillList.contains(skillName)) {
							addNewSkill.add(CategoryDTO.builder()
												.pId(Define.SERVICE_ID)
												.name(skillName.toUpperCase())
												.build());
						}
						tempSkillList.clear();
					}
					
				}
			}

			// 새로운 스킬들 추가
			if (!addNewSkill.isEmpty()) {
				// TODO test table에 중복값 확인. 실제 반영 데이터 확인 필
			    crawlingRepository.insertNewSkill(addNewSkill);
			    addNewSkill.clear();
			}
			
			// 새로운 qualifications_skill에 변환된 객체 추가
			newQualificationsSkill.add(category, skillsObject);
		}

		// 최종 변환된 qualifications_skill을 outputObject에 추가
		outputObject.addProperty("id", gptJsonElement.get("id").getAsLong());
		outputObject.add("qualifications_skill", newQualificationsSkill);

		return outputObject;
	}

	public List<JsonArray> getFilterNoticeToJsonListByGptPrompt(JsonArray gptJsonArray) {
		List<JsonArray> jsonArrayBySuccessAndFail = new ArrayList<>();

		Map<String, List<CategoryDTO>> skillCategorys = dataService.getSkillAllCategory();

		// 카테고리 리스트 가져오기
		List<CategoryDTO> languageList = skillCategorys.get("languageList");
		List<CategoryDTO> frameworkList = skillCategorys.get("frameworkList");
		List<CategoryDTO> SQLList = skillCategorys.get("SQLList");
		List<CategoryDTO> NoSQLList = skillCategorys.get("NoSQLList");
		List<CategoryDTO> devOpsList = skillCategorys.get("devOpsList");
		List<CategoryDTO> serviceList = skillCategorys.get("serviceList");

		JsonArray resultSuccessArray = new JsonArray();
		JsonArray resultFailArray = new JsonArray();

		// JsonArray의 각 객체를 순회
		for (JsonElement element : gptJsonArray) {
			JsonObject originalObject = element.getAsJsonObject();
			JsonObject qualificationsSkill = originalObject.getAsJsonObject("qualifications_skill");

			boolean isSuccess = true;

			// 카테고리별로 스킬 체크
			if (!containsAllSkills(qualificationsSkill.getAsJsonArray("Language"), languageList)) {
				isSuccess = false;
			}
			if (!containsAllSkills(qualificationsSkill.getAsJsonArray("Framework"), frameworkList)) {
				isSuccess = false;
			}
			if (!containsAllSkills(qualificationsSkill.getAsJsonArray("SQL"), SQLList)) {
				isSuccess = false;
			}
			if (!containsAllSkills(qualificationsSkill.getAsJsonArray("NoSQL"), NoSQLList)) {
				isSuccess = false;
			}
			if (!containsAllSkills(qualificationsSkill.getAsJsonArray("DevOps"), devOpsList)) {
				isSuccess = false;
			}
			if (!containsAllSkills(qualificationsSkill.getAsJsonArray("Service"), serviceList)) {
				isSuccess = false;
			}

			// 성공 여부에 따라 다른 배열에 추가
			if (isSuccess) {
				resultSuccessArray.add(originalObject);
			} else {
				resultFailArray.add(originalObject);
			}
		}

		// 성공과 실패 배열을 리스트에 추가하여 반환
		jsonArrayBySuccessAndFail.add(resultSuccessArray);
		jsonArrayBySuccessAndFail.add(resultFailArray);

		return jsonArrayBySuccessAndFail;
	}

	// 스킬들이 리스트에 모두 포함되는지 확인하는 메서드
	private boolean containsAllSkills(JsonArray skillsArray, List<CategoryDTO> categoryList) {
		if (skillsArray == null || skillsArray.size() == 0) {
			return true; // 스킬이 없으면 자동으로 포함된 것으로 간주
		}

		for (JsonElement skillElement : skillsArray) {
			String skill = skillElement.getAsString().split("/")[0]; // 스킬 이름 추출
			boolean found = false;
			for (CategoryDTO category : categoryList) {
				if (category.getName().equalsIgnoreCase(skill)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false; // 스킬이 리스트에 없으면 false 반환
			}
		}
		return true; // 모든 스킬이 리스트에 있으면 true 반환
	}

	// Origin Notice Json 데이터 insert 메소드
	@Transactional
	public int insertOriginNoticeJson(JsonArray noticeJsonArray) {
		int rowCount = 0;
		List<String> originNoticeJson = new ArrayList<>();

		for (JsonElement jsonElement : noticeJsonArray) {
			originNoticeJson.add(jsonElement.toString());
		}

		// TODO 한 번에 배치 처리로 insert
		rowCount = crawlingRepository.insertOriginNoticeJson(originNoticeJson);

		// notice id 넣기
		crawlingRepository.updateNoticeId();

		return rowCount;
	}

	public int insertNoticeJsonByGpt(JsonArray promptJsonArray, JsonArray gptJsonArray) {
		int rowCount = 0;

		if (!gptJsonArray.isEmpty()) {
			List<JsonNoticeSkill> skillJsonList = new ArrayList<>();

			Map<Integer, String> promptJsonMap = new HashMap<>();
			Map<Integer, String> mongoJsonMap = new HashMap<>();

			List<JsonArray> getFilterNoticeToJsonListByGptPrompt = getFilterNoticeToJsonListByGptPrompt(gptJsonArray);

			JsonArray completionJsonArray = getFilterNoticeToJsonListByGptPrompt.get(0);
			JsonArray completionMongoJsonArray = convertMongoDataArray(completionJsonArray);

			for (JsonElement jsonElement : promptJsonArray) {
				if (JsonUtil.getIdValue(jsonElement) != null) {
					promptJsonMap.put(JsonUtil.getIdValue(jsonElement), jsonElement.toString());
				}
			}

			for (JsonElement jsonElement : completionMongoJsonArray) {
				if (JsonUtil.getIdValue(jsonElement) != null) {
					mongoJsonMap.put(JsonUtil.getIdValue(jsonElement), jsonElement.toString());
				}
			}

			for (JsonElement jsonElement : completionJsonArray) {
				Integer noticeId = JsonUtil.getIdValue(jsonElement);
				if (noticeId != null) {
					skillJsonList.add(JsonNoticeSkill.builder().noticeId(noticeId)
							.crawlJson(promptJsonMap.get(noticeId)).gptJson(jsonElement.toString())
							.mongoJson(mongoJsonMap.get(noticeId)).completion(true).build());

				}
			}

			JsonArray modifiableJsonArray = getFilterNoticeToJsonListByGptPrompt.get(1);

			for (JsonElement jsonElement : modifiableJsonArray) {
				Integer noticeId = JsonUtil.getIdValue(jsonElement);
				if (noticeId != null) {
					skillJsonList
							.add(JsonNoticeSkill.builder().noticeId(noticeId).crawlJson(promptJsonMap.get(noticeId))
									.gptJson(jsonElement.toString()).completion(false).build());

				}
			}

			rowCount = crawlingRepository.insertSkillNoticeJson(skillJsonList);

		}
		return rowCount;
	}
	
	public void addSkill() {
		Map<String, List<CategoryDTO>> skillCategorys = dataService.getSkillAllCategory();

		// 카테고리 리스트 가져오기
		List<CategoryDTO> languageList = skillCategorys.get("languageList");
		List<CategoryDTO> frameworkList = skillCategorys.get("frameworkList");
		List<CategoryDTO> SQLList = skillCategorys.get("SQLList");
		List<CategoryDTO> NoSQLList = skillCategorys.get("NoSQLList");
		List<CategoryDTO> devOpsList = skillCategorys.get("devOpsList");
		List<CategoryDTO> serviceList = skillCategorys.get("serviceList");
	}
	
	public void duplicateSkillCheck(List<CategoryDTO> categoryList, List<CategoryDTO> newSkillList, List<String> skillList, String skillName, Integer categoryId) {
		for (CategoryDTO categoryDTO : categoryList) {
			skillList.add(categoryDTO.getName());
		}
		
		if (!skillList.contains(skillName)) {
			newSkillList.add(CategoryDTO.builder()
								.pId(categoryId)
								.name(skillName.toUpperCase())
								.build());
		}
		skillList.clear();
	}
	
}
