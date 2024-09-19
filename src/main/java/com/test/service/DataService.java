package com.test.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenco.perfectfolio.dto.admin.CategoryDTO;
import com.tenco.perfectfolio.dto.admin.SingleOriginDataDTO;
import com.tenco.perfectfolio.repository.interfaces.admin.DataRepository;
import com.tenco.perfectfolio.repository.model.admin.JsonlModel;
import com.tenco.perfectfolio.utils.Define;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataService {

    @Autowired
    private DataRepository dataRepository;



    @Transactional
    public Integer getSingleOriginDataId() {
        return dataRepository.getSingleOriginDataId();
    }

	@Transactional
	public Integer getRandomOriginDataId() {
		return dataRepository.getRandomOriginDataId();
	}
    @Transactional
    public String getSingleOriginData(int id) {
        return dataRepository.getSingleOriginData(id);
    }

	@Transactional
	public void insertCategory(String tableName, String categoryName) {
		dataRepository.insertCategory(tableName, categoryName);
	}

	@Transactional
	public void insertResultAndResponseData(Integer id, String resultData, String responseData) {

		dataRepository.insertResultAndResponseData(id, resultData, responseData);

	}

    // 카테고리 전체를 출력하기 위한 인터페이스 호출
	@Transactional
    public Map<String, List<CategoryDTO>> getAllCategory() {
    	
    	// 카테고리 전체가 들어있는 리스트 생성
    	List<CategoryDTO> allCategoryList = dataRepository.getAllCategory();
    	
    	// 각 카테고리 분류별 리스트 생성
    	// !! 주의 --> Define에 정의된 값 기준으로 순서를 정렬 Why? 인덱스와 Define의 값이 동일하다
    	List<CategoryDTO> mainCategoryList = new ArrayList<>();
    	List<CategoryDTO> categoryList = new ArrayList<>();
    	List<CategoryDTO> projectCategoryList = new ArrayList<>();
    	List<CategoryDTO> qualificationsSkillList = new ArrayList<>();
		List<CategoryDTO> qualificationsOptionList = new ArrayList<>();
		List<CategoryDTO> preferredOptionList = new ArrayList<>();
    	
    	// allCategoryList를 분류하여 각 카테고리 리스트에 저장
    	for (CategoryDTO dto : allCategoryList) {
    		if(dto.getCategoryType().equals(Define.MAIN_CATEGORY)) {
    			mainCategoryList.add(dto);
    		} else if (dto.getCategoryType().equals(Define.CATEGORY)) {
    			categoryList.add(dto);
    		} else if (dto.getCategoryType().equals(Define.PROJECT_CATEGORY)) {
    			projectCategoryList.add(dto);
    		} else if (dto.getCategoryType().equals(Define.QUALIFICATIONS_SKILL)) {
    			qualificationsSkillList.add(dto);
    		} else if (dto.getCategoryType().equals(Define.QUALIFICATIONS_OPTION)) {
				qualificationsOptionList.add(dto);
			} else if (dto.getCategoryType().equals(Define.PREFERRED_OPTION)) {
				preferredOptionList.add(dto);
			} else {
    			// 카테고리 타입이 정의되어 있지 않으면 null 값을 리턴
    			return null;
    		}
    	}
    	
    	// 각 카테고리 리스트를 Map 자료구조로 리턴
    	Map<String, List<CategoryDTO>> processCategoryMap = new HashMap<>();
    	processCategoryMap.put("mainCategoryList", mainCategoryList);
    	processCategoryMap.put("categoryList", categoryList);
    	processCategoryMap.put("projectCategoryList", projectCategoryList);
    	processCategoryMap.put("qualificationsSkillList", qualificationsSkillList);
    	processCategoryMap.put("qualificationsOptionList", qualificationsOptionList);
		processCategoryMap.put("preferredOptionList", preferredOptionList);
    	return processCategoryMap;
    }
	
	// 카테고리 전체를 출력하기 위한 인터페이스 호출
		@Transactional
	    public Map<String, List<CategoryDTO>> getSkillAllCategory() {
	    	
	    	// 카테고리 전체가 들어있는 리스트 생성
	    	List<CategoryDTO> allSkillCategoryList = dataRepository.getSkillAllCategory();
	    	
	    	// 각 카테고리 분류별 리스트 생성
	    	// !! 주의 --> Define에 정의된 값 기준으로 순서를 정렬 Why? 인덱스와 Define의 값이 동일하다
	    	List<CategoryDTO> languageList = new ArrayList<>();
	    	List<CategoryDTO> frameworkList = new ArrayList<>();
	    	List<CategoryDTO> SQLList = new ArrayList<>();
	    	List<CategoryDTO> NoSQLList = new ArrayList<>();
			List<CategoryDTO> devOpsList = new ArrayList<>();
			List<CategoryDTO> serviceList = new ArrayList<>();
	    	
	    	// allCategoryList를 분류하여 각 카테고리 리스트에 저장
	    	for (CategoryDTO dto : allSkillCategoryList) {
	    		if(dto.getPId().equals(Define.LANGUAGE_ID)) {
	    			languageList.add(dto);
	    		} else if (dto.getPId().equals(Define.FRAMEWORK_ID)) {
	    			frameworkList.add(dto);
	    		} else if (dto.getPId().equals(Define.SQL_ID)) {
	    			SQLList.add(dto);
	    		} else if (dto.getPId().equals(Define.NOSQL_ID)) {
	    			NoSQLList.add(dto);
	    		} else if (dto.getPId().equals(Define.DEVOPS_ID)) {
	    			devOpsList.add(dto);
				} else if (dto.getPId().equals(Define.SERVICE_ID)) {
					serviceList.add(dto);
				} else {
	    			// 카테고리 타입이 정의되어 있지 않으면 null 값을 리턴
	    			return null;
	    		}
	    	}
	    	
	    	// 각 카테고리 리스트를 Map 자료구조로 리턴
	    	Map<String, List<CategoryDTO>> processCategoryMap = new HashMap<>();
	    	processCategoryMap.put("languageList", languageList);
	    	processCategoryMap.put("frameworkList", frameworkList);
	    	processCategoryMap.put("SQLList", SQLList);
	    	processCategoryMap.put("NoSQLList", NoSQLList);
	    	processCategoryMap.put("devOpsList", devOpsList);
			processCategoryMap.put("serviceList", serviceList);
	    	return processCategoryMap;
	    }

	@Transactional
	public void	makeJsonlFile() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		List<JsonlModel> datas = dataRepository.makeJsonlFile();


		// 파일 경로 설정 (static 폴더 내)
		String filePath = "/Users/gimdong-yun/Documents/output.jsonl";
		File file = new File(filePath);
		//System.out.println(datas.toString());
		try (FileWriter writer = new FileWriter(file)) {
			for (JsonlModel model : datas) {
				//writer.write(model.getResponseJsonl() + ", " + model.getResultJsonl() + "\n");
				String line = "{\"Text\": \"" + model.getRequestJsonl()
						+ "\", \"Completion\": \"" + model.getResponseJsonl()
						+ "\"}";

				//String jsonString = objectMapper.writeValueAsString(line);
				writer.write(line + "\n");
				System.out.println("dd");
			}
		}
	}
}
