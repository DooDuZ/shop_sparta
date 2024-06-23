package com.sparta.shop_sparta.validator.member.pattern;

import java.util.regex.Pattern;

public class PatternConfig {

    // 정규식
    private static final String passwordRegex = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=])(?=.*[0-9]).{10,16}$";
    private static final String loginIdRegex = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{5,25}$";
    private static final String emailRegex = "^[A-z0-9]{2,20}+@[A-z]{2,20}+\\.[a-z]{2,3}$";
    private static final String phoneNumberRegex = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";


    // 컴파일 패턴
    public static final Pattern passwordPattern = Pattern.compile(passwordRegex);
    public static final Pattern loginIdPattern = Pattern.compile(loginIdRegex);
    public static final Pattern emailPattern = Pattern.compile(emailRegex);
    public static final Pattern phoneNumberPattern = Pattern.compile(phoneNumberRegex);
}
