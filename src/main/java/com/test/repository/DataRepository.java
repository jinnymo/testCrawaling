package com.test.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.test.dto.CategoryDTO;
import com.test.repository.model.JsonlModel;

@Mapper
public interface DataRepository {

    public Integer getSingleOriginDataId();
    public Integer getRandomOriginDataId();

    public String getSingleOriginData(int id);

    public void insertCategory(String tableName,String categoryName);

    public void insertResultAndResponseData(Integer id, String resultData, String responseData);

    public List<JsonlModel> makeJsonlFile();

    public String putSingleOriginData(int id);

    // 카테고리 전체 출력을 위한 인터페이스
    public List<CategoryDTO> getAllCategory(); 
    
    // 사용자 기술 스택 생성을 위한 카테고리 전체 출력을 위한 인터페이스
    public List<CategoryDTO> getSkillAllCategory(); 
    
    
}
