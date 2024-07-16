package com.sparta.shop_sparta.constant.member;

import org.springframework.http.HttpStatus;

public enum AuthMessage {
    INVALID_TOKEN("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_PRINCIPLE("인증 정보 불일치", HttpStatus.UNAUTHORIZED),
    AUTHORIZATION_DENIED("접근 권한 없음", HttpStatus.UNAUTHORIZED),
    ;

    private final String message;
    private HttpStatus httpStatus;

    AuthMessage(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return this.message;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
