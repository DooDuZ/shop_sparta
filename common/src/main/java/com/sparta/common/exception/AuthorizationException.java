package com.sparta.common.exception;

import com.sparta.common.constant.member.AuthMessage;
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
