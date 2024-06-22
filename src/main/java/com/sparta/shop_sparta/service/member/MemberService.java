package com.sparta.shop_sparta.service.member;

import com.sparta.shop_sparta.domain.dto.member.LoginResponseDto;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;

public interface MemberService {
    MemberDto createAccount(MemberDto memberDto);
    LoginResponseDto login(MemberDto memberDTO);
    void logout();
    void updatePassword(String currentPassword, String newPassword);
    void updatePhoneNumber(String PhoneNumber);
}
