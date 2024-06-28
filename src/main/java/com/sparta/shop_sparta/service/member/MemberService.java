package com.sparta.shop_sparta.service.member;

import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.dto.member.MemberUpdateRequestVo;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    ResponseEntity<?> createAccount(MemberDto memberDto);
    ResponseEntity<?> updatePassword(MemberUpdateRequestVo passwordUpdateRequestDto);
    ResponseEntity<?> updatePhoneNumber(MemberUpdateRequestVo phoneNumberUpdateRequestDto);

    ResponseEntity<?> verifySignup(Long memberId, String verificationCode);

    ResponseEntity<?> getMemberInfo(Long memberId);
}
