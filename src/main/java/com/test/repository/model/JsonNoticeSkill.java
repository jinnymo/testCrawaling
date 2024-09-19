package com.test.repository.model;

import java.sql.Timestamp;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class JsonNoticeSkill {

	Integer id;
	Integer noticeId;
	String crawlJson;
	String gptJson;
	String mongoJson;
	Timestamp createdAt;
	boolean completion;
	
}
