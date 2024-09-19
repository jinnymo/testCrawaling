package com.test.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.test.repository.model.JsonNoticeSkill;

@Mapper
public interface CrawlingRepository {

	// 새로 수집한 공고가 기존에 존재하는 지 확인
	List<Integer> getNoticeIdList();
	
	// 원본 크롤링 데이터 JSON 테이블의 notice Id 설정 메소드
	void updateNoticeId();
	
	// 가공 크롤링 데이터 JSON 테이블의 notice Id 설정 메소드
	void updateNoticeIdByGpt();
	
	// 원본 크롤링 데이터 Json insert
	Integer insertOriginNoticeJson(List<String> originNoticeJson);
	
	// 가공 크롤링 데이터 Json insert
	Integer insertSkillNoticeJson(List<JsonNoticeSkill> skillNoticeSkill);
	
	// 새로운 스킬 추가
	Integer insertNewSkill(List<CategoryDTO> newSkillList);
	
}
