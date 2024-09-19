package com.test.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.test.repository.JsonNoticeRepository;
import com.test.repository.entity.NoticeEntity;

@Service
public class NoticeModifyService {

    @Autowired
    private JsonNoticeRepository jsonNoticeRepository;
    @Autowired
    private CrawlingService crawlingService;

    // completion이 false인 데이터만 가져오기
    public List<NoticeEntity> getIncompleteNotices() {
        return jsonNoticeRepository.findByCompleionFalse();
    }

    // 특정 Notice 가져오기
    public NoticeEntity getNoticeById(Long id) {
        return jsonNoticeRepository.findById(id).orElse(null);
    }

    // GPT JSON을 JsonObject로 변환
    public JsonObject getGptJsonAsObject(Long noticeId) {
    	NoticeEntity notice = getNoticeById(noticeId);
        if (notice != null && notice.getGptJson() != null) {
            return JsonParser.parseString(notice.getGptJson()).getAsJsonObject();
        }
        return null;
    }

    // GPT JSON 업데이트
    public void updateGptJson(Long noticeId, JsonObject updatedGptJson) {
    	NoticeEntity notice = getNoticeById(noticeId);
        if (notice != null) {
            notice.setGptJson(updatedGptJson.toString());
            notice.setMongoJson(crawlingService.convertMongoDataObject(updatedGptJson).toString());
            notice.setCompleion(true);
            jsonNoticeRepository.save(notice);
        }
    }
}
