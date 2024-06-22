package com.sparta.shop_sparta.validator.password;

public interface PasswordValidator {
    Boolean checkPattern(String password);
    Boolean checkSame(String password, String newPassword);
}
