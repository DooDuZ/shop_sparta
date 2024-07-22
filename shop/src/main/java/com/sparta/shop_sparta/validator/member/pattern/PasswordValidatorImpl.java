package com.sparta.shop_sparta.validator.member.pattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidatorImpl extends MemberInfoValidator implements PasswordValidator{
    public PasswordValidatorImpl() {
        super();
    }

    @Override
    public Boolean checkSame(String password, String newPassword) {
        return password.equals(newPassword);
    }
}
