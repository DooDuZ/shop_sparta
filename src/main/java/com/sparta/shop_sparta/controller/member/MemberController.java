package com.sparta.shop_sparta.controller.member;

import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.dto.member.MemberUpdateRequestVo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface MemberController {
    ResponseEntity<?> createAccount(MemberDto memberDto);
    ResponseEntity<?> updatePassword(UserDetails userDetails, MemberUpdateRequestVo passwordUpdateRequestDto);
    ResponseEntity<?> updatePhoneNumber(UserDetails userDetails, MemberUpdateRequestVo phoneNumberUpdateRequestDto);
    ResponseEntity<?> verifySignup(Long memberId, String verificationCode);
    ResponseEntity<?> getMemberInfo(UserDetails userDetails, Long memberId);
}
