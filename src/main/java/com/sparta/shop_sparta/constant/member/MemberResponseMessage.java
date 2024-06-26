package com.sparta.shop_sparta.constant.member;

public enum MemberResponseMessage {
    MISSING_REQUIRED_FIELD("필수 정보 누락"),
    DUPLICATED_LOGIN_ID("존재하는 아이디"),
    DUPLICATED_EMAIL("존재하는 이메일"),
    UNMATCHED_ID("아이디 형식 오류"),
    UNMATCHED_EMAIL("이메일 형식 오류"),
    UNMATCHED_PHONENUMBER("전화번호 형식 오류"),
    UNMATCHED_PASSWORD("비밀번호 형식 오류"),
    NOT_FOUND("회원 정보를 찾지 못했습니다"),
    UNMATCHED_VERIFICATION_CODE("인증 코드 불일치"),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다"),
    SUCCESS_LOGIN("로그인 성공"),
    NOT_SEARCH_ADDR("없는 주소 입니다."),
    MAX_SAVE_LIMIT("최대 주소 수 초과"),
    ;
    private final String message;
    MemberResponseMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
