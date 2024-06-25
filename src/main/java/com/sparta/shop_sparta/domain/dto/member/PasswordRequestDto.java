package com.sparta.shop_sparta.domain.dto.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PasswordRequestDto {
    String password;
    String confirmPassword;
}
