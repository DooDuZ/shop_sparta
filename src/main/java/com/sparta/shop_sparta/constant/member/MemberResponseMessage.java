package com.sparta.shop_sparta.constant.member;

public enum MemberResponseMessage {
    MISSING_REQUIRED_FIELD("필수 정보 누락"),
    DUPLICATED_LOGIN_ID("존재하는 아이디"),
    DUPLICATED_EMAIL("존재하는 이메일"),
    UNMATCHED_ID("아이디 형식 오류"),
    UNMATCHED_EMAIL("이메일 형식 오류"),
    UNMATCHED_PHONENUMBER("전화번호 형식 오류"),
    UNMATCHED_PASSWORD("비밀번호 형식 오류"),
    ;
    private final String message;
    MemberResponseMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
