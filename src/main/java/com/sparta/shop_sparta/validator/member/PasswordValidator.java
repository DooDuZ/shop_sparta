package com.sparta.shop_sparta.validator.member;

public interface PasswordValidator {
    Boolean checkPattern(String password);
    Boolean checkSame(String password, String newPassword);
}
