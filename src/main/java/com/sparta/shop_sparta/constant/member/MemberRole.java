package com.sparta.shop_sparta.constant.member;

import lombok.Getter;

@Getter
public enum MemberRole {
    ADMIN("ADMIN"),
    BASIC("BASIC"),
    UNVERIFIED("UNVERIFIED"),
    ;

    private final String grade;

    MemberRole(String grade){
        this.grade = grade;
    }
}
