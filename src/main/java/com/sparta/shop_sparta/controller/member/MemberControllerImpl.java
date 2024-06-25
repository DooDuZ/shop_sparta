package com.sparta.shop_sparta.controller.member;

import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.dto.member.PasswordRequestDto;
import com.sparta.shop_sparta.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberControllerImpl implements MemberController{

    private final MemberService memberService;

    @Override
    @PostMapping("/")
    public ResponseEntity<?> createAccount(@RequestBody MemberDto memberDTO) {
        return memberService.createAccount(memberDTO);
    }

    @Override
    @GetMapping("/verification")
    public ResponseEntity<?> verifySignup(@RequestParam("memberId") Long memberId, @RequestParam("verificationCode") String verificationCode) {
        return memberService.verifySignup(memberId, verificationCode);
    }

    @Override
    @PatchMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordRequestDto passwordRequestDto) {
        return memberService.updatePassword(passwordRequestDto);
    }

    @Override
    @PatchMapping("/phoneNumber")
    public ResponseEntity<?> updatePhoneNumber(String PhoneNumber) {
        return null;
    }

    @GetMapping("/authorize")
    public String test(){
        return "Success!";
    }
}
