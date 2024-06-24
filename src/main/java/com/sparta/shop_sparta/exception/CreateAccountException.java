package com.sparta.shop_sparta.exception;

public class CreateAccountException extends IllegalArgumentException{

    public CreateAccountException(String message) {
        super(message);
    }

    public CreateAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateAccountException(Throwable cause) {
        super(cause);
    }
}
