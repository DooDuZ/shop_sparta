package com.sparta.shop_sparta.validator.member;

public interface PasswordValidator {
    Boolean checkPasswordPattern(String password);
    Boolean checkSamePassword(String password, String newPassword);
}
