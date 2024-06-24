package com.sparta.shop_sparta.controller.member;

import com.sparta.shop_sparta.domain.dto.member.LoginResponseDto;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;

public interface MemberController {
    MemberDto createAccount(MemberDto memberDTO);
    LoginResponseDto login(MemberDto memberDTO);
    void logout();
    void updatePassword(String currentPassword, String newPassword);
    void updatePhoneNumber(String PhoneNumber);

    Boolean verifySignup(Long memberId, String verificationCode);
}
