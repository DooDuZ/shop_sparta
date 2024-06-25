package com.sparta.shop_sparta.controller.member;

import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.dto.member.PasswordRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface MemberController {
    ResponseEntity<?> createAccount(MemberDto memberDTO);
    ResponseEntity<?> updatePassword(PasswordRequestDto passwordRequestDto);
    ResponseEntity<?> updatePhoneNumber(String PhoneNumber);
    ResponseEntity<?> verifySignup(Long memberId, String verificationCode);
}
