package com.sparta.shop_sparta.constant.member;

public enum MemberResponseMessage {
    MISSING_REQUIRED_FIELD("필수 정보 누락"),
    DUPLICATED_LOGIN_ID("존재하는 아이디"),
    DUPICATED_EMAIL("존재하는 이메일");
    private final String message;
    MemberResponseMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
