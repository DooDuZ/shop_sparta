package com.sparta.shop_sparta.exception.member;

public class MemberAuthorizeException extends IllegalArgumentException{

    public MemberAuthorizeException(String message) {
        super(message);
    }

    public MemberAuthorizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberAuthorizeException(Throwable cause) {
        super(cause);
    }
}
