package com.sparta.shop_sparta.service.member;

import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.dto.member.MemberRequestVo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface MemberService {
    ResponseEntity<?> createAccount(MemberDto memberDto);
    ResponseEntity<?> getMemberInfo(UserDetails userDetails, Long memberId);
    ResponseEntity<?> updatePassword(UserDetails userDetails, MemberRequestVo passwordUpdateRequestDto);
    ResponseEntity<?> updatePhoneNumber(UserDetails userDetails, MemberRequestVo phoneNumberUpdateRequestDto);

    ResponseEntity<?> verifySignup(Long memberId, String verificationCode);
}
