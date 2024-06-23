package com.sparta.shop_sparta.validator.member;

public interface PasswordValidator extends PatternValidator{
    Boolean checkSame(String password, String newPassword);
}
