package com.sparta.shop_sparta.validator.member;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidatorImpl implements PasswordValidator{
    private final String passwordRegex = PatternConfig.passwordRegex;
    private final Pattern passwordPattern = Pattern.compile(passwordRegex);

    @Override
    public Boolean checkPattern(String password) {
        Matcher matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }

    @Override
    public Boolean checkSame(String password, String newPassword) {
        return password.equals(newPassword);
    }
}
