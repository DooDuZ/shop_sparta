package com.sparta.shop_sparta.controller.member;

import com.sparta.shop_sparta.domain.dto.member.LoginResponseDTO;
import com.sparta.shop_sparta.domain.dto.member.MemberDTO;

public interface MemberController {
    MemberDTO createAccount(MemberDTO memberDTO);
    LoginResponseDTO login(MemberDTO memberDTO);
    void logout();
    void updatePassword(String currentPassword, String newPassword);
    void updatePhoneNumber(String PhoneNumber);
}
