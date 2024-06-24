package com.sparta.shop_sparta.controller.member;

import com.sparta.shop_sparta.domain.dto.member.LoginResponseDto;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.service.member.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
    public MemberDto createAccount(@RequestBody MemberDto memberDTO) {
        return memberService.createAccount(memberDTO);
    }

    @Override
    @PostMapping("/login")
    public LoginResponseDto login(String username, String password, HttpServletResponse httpServletResponse) {
        return memberService.login(username, password, httpServletResponse);
    }

    @Override
    @GetMapping("/verification")
    public Boolean verifySignup(@RequestParam("memberId") Long memberId, @RequestParam("verificationCode") String verificationCode) {
        return memberService.verifySignup(memberId, verificationCode);
    }

    @Override
    public void logout() {

    }

    @Override
    public void updatePassword(String currentPassword, String newPassword) {

    }

    @Override
    public void updatePhoneNumber(String PhoneNumber) {

    }
}
