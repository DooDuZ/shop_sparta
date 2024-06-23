package com.sparta.shop_sparta.validator.member;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidatorImpl implements PasswordValidator{

    private final String passwordRegex = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=])(?=.*[0-9]).{10,16}$";

    private final Pattern passwordPattern = Pattern.compile(passwordRegex);

    @Override
    public Boolean checkPasswordPattern(String password) {
        Matcher matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }

    @Override
    public Boolean checkSamePassword(String password, String newPassword) {
        return password.equals(newPassword);
    }
}
