package com.test.repository.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"crawlJson", "gptJson", "mongoJson"})
public class NoticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "notice_id")
    private Long noticeId;

    @Column(name = "crawl_json", columnDefinition = "JSON")
    private String crawlJson;  // MySQL JSON 타입을 String으로 매핑

    @Column(name = "gpt_json", columnDefinition = "JSON")
    private String gptJson;    // MySQL JSON 타입을 String으로 매핑

    @Column(name = "mongo_json", columnDefinition = "JSON")
    private String mongoJson;  // MySQL JSON 타입을 String으로 매핑

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "compleion")
    private boolean compleion;
}