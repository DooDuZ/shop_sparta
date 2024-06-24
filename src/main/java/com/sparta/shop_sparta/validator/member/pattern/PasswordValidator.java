package com.sparta.shop_sparta.validator.member.pattern;

public interface PasswordValidator extends PatternValidator{
    Boolean checkSame(String password, String newPassword);
}
