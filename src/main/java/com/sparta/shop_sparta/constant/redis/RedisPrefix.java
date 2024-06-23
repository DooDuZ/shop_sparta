package com.sparta.shop_sparta.constant.redis;

public enum RedisPrefix {
    SIGNUP_VERIFICATION("signup-code");
    final String message;

    RedisPrefix(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
