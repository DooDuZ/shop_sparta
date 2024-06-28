package com.sparta.shop_sparta.constant.member;

public enum AuthMessage {
    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    INVALID_PRINCIPLE("인증 정보 불일치"),
    FAIL_CONVERT_TO_JSON("토큰 JSON 파싱 오류"),
    AUTHORIZATION_DENIED("접근 권한 없음"),
    ;

    private final String message;

    AuthMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
