package com.test.utils;

public class Define {

	//홈 url
	//TODO - 서비스 배포시 변경 (ex. http://jinnymo.com)
	public static final String HOME_URL = "http://localhost:8080";


	// 이미지 관련
	public static final String UPLOAD_FILE_DERECTORY = "C:\\Users\\user\\git\\perfect_folio\\src\\main\\resources\\static\\images/";
	public static final int MAX_FILE_SIZE = 1024 * 1024 * 20; // 20MB

	// REST api 요청 URL
	public static final String NAVER_REQUEST_ACCESSTOKEN_URL = "https://nid.naver.com/oauth2.0/token";
	public static final String NAVER_REQUEST_USERINFO_URL = "https://openapi.naver.com/v1/nid/me";
	public static final String KAKAO_REQUEST_ACCESSTOKEN_URL = "https://kauth.kakao.com/oauth/token";
	public static final String KAKAO_REQUEST_USERINFO_URL = "https://kapi.kakao.com/v2/user/me";

	public static final String GOOGLE_REQUEST_ACCESSTOKEN_URL = "https://oauth2.googleapis.com/token";
	public static final String GOOGLE_REQUEST_USERINFO_URL = "https://www.googleapis.com/userinfo/v2/me?access_token=";

	// 소셜 로그인 타입
	public static final String SOCIAL_TYPE_IS_LOCAL = "local";
	public static final String SOCIAL_TYPE_IS_NAVER = "naver";
	public static final String SOCIAL_TYPE_IS_GOOGLE = "google";
	public static final String SOCIAL_TYPE_IS_KAKAO = "kakao";
	public static final String SOCIAL_TYPE_IS_ENTERPRISE = "com";

	// 소셜 로그인 비밀번호 할당
	public static final String SOCIAL_PASSWORD_GOOGLE = "OAuth_Google";
	public static final String SOCIAL_PASSWORD_NAVER = "OAuth_Naver";
	public static final String SOCIAL_PASSWORD_KAKAO = "OAuth_Kakao";

	// 카테고리 전체 불러오기 위한 Define
	public static final Integer MAIN_CATEGORY = 0;
	public static final Integer CATEGORY = 1;
	public static final Integer PROJECT_CATEGORY = 2;
	public static final Integer QUALIFICATIONS_SKILL = 3;
	public static final Integer QUALIFICATIONS_OPTION = 4;
	public static final Integer PREFERRED_OPTION = 5;

	// 회원가입 및 로그인
	public static final String SIGNUP_NAME = "이름: 공백 없이 10자 이하로 영어 또는 한글로 입력해주세요.";
	public static final String SIGNUP_ID = "아이디: 공백 없이 영어로 입력해주세요.";
	public static final String SIGNUP_PWD = "비밀번호: 공백 없이 8자 이상 ~ 20자 이하로 영어/숫자/특수문자(!@#$%^&*) 포함하여 입력해주세요.";
	public static final String SIGNUP_EMAIL = "이메일: 공백 없이 올바른 이메일 형식을 입력해주세요.";
	public static final String SIGNUP_BIRTH = "생년월일: 생년월일을 입력해주세요.";
	public static final String SIGNUP_TEL = "전화번호: 올바른 형식의 전화번호를 입력해주세요.";
	public static final String PASSWORD_DISMATCH = "비밀번호와 확인 비밀번호가 일치하지 않습니다.";
	public static final String PASSWORD_MATCH_NOT_CHANGE = "현재 비밀번호와 변경할 비밀번호가 일치합니다. 변경할 비밀번호를 입력해주세요.";
	public static final String SIGNIN_ID = "아이디를 다시 입력해주세요.";
	public static final String SIGNIN_PWD = "공백 없이 비밀번호를 입력해주세요.";
	public static final String SIGNIN_PWD_AGAIN = "비밀번호가 일치하지 않습니다. 다시 입력해주세요.";
	public static final String LOGIN_USER_NOT_EXIST = "존재하지 않는 회원입니다. 회원가입 후 사용해주세요.";
	public static final String PROCESS_FAIL = "정상 처리 되지 않았습니다.";
	public static final String WITHDRAW_REASON_SELECT = "탈퇴 사유를 선택해주세요.";

	
	
	// HTTP 상태 코드
	public static final String ENTER_YOUR_LOGIN = "로그인 먼저 해주세요.";
	public static final String NOT_AN_AUTHENTICATED_USER = "인증된 사용자가 아닙니다.";
	public static final String FAILED_SUBSCRIBE = "이미 구독중인 상품이 존재합니다.";
	public static final String FAILED_PAYMENT = "결제에 실패하였습니다.";
	
	// 결제
	public static final String FAILED_PROCESS_PAYMENT = "Failed to process payment: ";
	public static final String FAILED_ISSUE_BILLINGKEY = "Failed to issue billing key: ";
	public static final String FAILED_CANCEL_PAYMENT = "Failed to cancel payment: ";

	// 채용공고 크롤링할 갯수
	public static final Integer NOTICE_EACH = 2;
	
	// 스킬 카테고리 명
	public static final String LANGUAGE_NAME = "Language";
	public static final String FRAMEWORK_NAME = "Framework";
	public static final String SQL_NAME = "SQL";
	public static final String NOSQL_NAME = "NoSQL";
	public static final String DEVOPS_NAME = "DevOps";
	public static final String SERVICE_NAME = "Service";
	
	// 사용자 기술 스택 생성을 위한 카테고리 전체 Define
	public static final Integer LANGUAGE_ID = 1;
	public static final Integer FRAMEWORK_ID = 2;
	public static final Integer SQL_ID = 3;
	public static final Integer NOSQL_ID = 4;
	public static final Integer DEVOPS_ID = 5;
	public static final Integer SERVICE_ID = 6;
}
