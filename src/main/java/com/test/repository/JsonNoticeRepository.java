package com.test.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.repository.entity.NoticeEntity;

@Repository
public interface JsonNoticeRepository extends JpaRepository<NoticeEntity, Long> {
    // compleion이 false인 데이터만 가져오기 위한 메서드
    List<NoticeEntity> findByCompleionFalse();
}
