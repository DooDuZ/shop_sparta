package com.sparta.shop_sparta.constant.member;

import org.springframework.http.HttpStatus;

public enum MemberResponseMessage {
    MISSING_REQUIRED_FIELD("필수 정보 누락", HttpStatus.BAD_REQUEST),
    DUPLICATED_LOGIN_ID("존재하는 아이디", HttpStatus.CONFLICT),
    DUPLICATED_EMAIL("존재하는 이메일", HttpStatus.CONFLICT),
    UNMATCHED_ID("아이디 형식 오류", HttpStatus.BAD_REQUEST),
    UNMATCHED_EMAIL("이메일 형식 오류", HttpStatus.BAD_REQUEST),
    UNMATCHED_PHONENUMBER("전화번호 형식 오류", HttpStatus.BAD_REQUEST),
    UNMATCHED_PASSWORD("비밀번호 형식 오류", HttpStatus.BAD_REQUEST),
    NOT_FOUND("회원 정보를 찾지 못했습니다", HttpStatus.NOT_FOUND),
    UNMATCHED_VERIFICATION_CODE("인증 코드 불일치", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST),
    SUCCESS_LOGIN("로그인 성공", HttpStatus.OK),
    NOT_SEARCH_ADDR("없는 주소 입니다.", HttpStatus.BAD_REQUEST),
    MAX_SAVE_LIMIT("최대 주소 수 초과", HttpStatus.BAD_REQUEST),
    FAIL_CONVERT_TO_JSON("토큰 JSON 파싱 오류", HttpStatus.UNAUTHORIZED),
    ;
    private final String message;
    private final HttpStatus httpStatus;

    MemberResponseMessage(String message, HttpStatus httpStatus){
        this.message = message;
        this.httpStatus = httpStatus;
    }
    public String getMessage(){
        return this.message;
    }

    public HttpStatus getHttpStatus(){
        return this.httpStatus;
    }
}
