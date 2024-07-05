package com.sparta.shop_sparta.service.member;

import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.dto.member.MemberRequestVo;
import com.sparta.shop_sparta.domain.dto.member.MemberResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface MemberService {
    ResponseEntity<?> createAccount(MemberDto memberDto);
    MemberResponseDto getMemberInfo(UserDetails userDetails, Long memberId);
    void updatePassword(UserDetails userDetails, MemberRequestVo passwordUpdateRequestDto);
    void updatePhoneNumber(UserDetails userDetails, MemberRequestVo phoneNumberUpdateRequestDto);

    ResponseEntity<?> verifySignup(Long memberId, String verificationCode);
}
