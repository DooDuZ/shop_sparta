package com.sparta.batch.constant;

import lombok.Getter;

@Getter
public enum MemberRole {
    ADMIN("ROLE_ADMIN"),
    BASIC("ROLE_BASIC"),
    SELLER("ROLE_SELLER"),
    UNVERIFIED("ROLE_UNVERIFIED"),
    ;

    private final String grade;

    MemberRole(String grade){
        this.grade = grade;
    }
}
