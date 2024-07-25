package com.sparta.shop_sparta.util.validator.member.pattern;

public interface PasswordValidator extends PatternValidator{
    Boolean checkSame(String password, String newPassword);
}
