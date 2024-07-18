package com.sparta.shop_sparta.exception;

import com.sparta.shop_sparta.constant.member.AuthMessage;
import lombok.Getter;

@Getter
public class AuthorizationException extends RuntimeException{

    private final AuthMessage error;

    public AuthorizationException(AuthMessage authMessage) {
        super(authMessage.getMessage());
        this.error = authMessage;
    }

    public AuthorizationException(AuthMessage authMessage, Throwable cause) {
        super(authMessage.getMessage(), cause);
        this.error = authMessage;
    }
}
