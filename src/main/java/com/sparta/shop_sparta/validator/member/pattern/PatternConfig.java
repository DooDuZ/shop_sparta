package com.sparta.shop_sparta.validator.member.pattern;

import java.util.regex.Pattern;

public class PatternConfig {

    // 정규식
    static final String passwordRegex = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=])(?=.*[0-9]).{10,16}$";
    static final String loginIdRegex = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{5,25}$";


    // 컴파일 패턴
    static final Pattern passwordPattern = Pattern.compile(passwordRegex);
    static final Pattern loginIdPattern = Pattern.compile(loginIdRegex);
    // static final String emailRegex = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=])(?=.*[0-9]).{10,16}$";
    // static final String phoneNumberRegex = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=])(?=.*[0-9]).{10,16}$";
}
