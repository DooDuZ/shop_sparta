package com.sparta.shop_sparta.validator.password;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidatorImpl implements PasswordValidator{

    private final String regex = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=])(?=.*[0-9]).{10,16}$";

    private final Pattern pattern = Pattern.compile(regex);

    @Override
    public Boolean checkPattern(String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    @Override
    public Boolean checkSame(String password, String newPassword) {
        return password.equals(newPassword);
    }
}
