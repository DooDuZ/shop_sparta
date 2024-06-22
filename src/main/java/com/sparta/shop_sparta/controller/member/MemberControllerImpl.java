package com.sparta.shop_sparta.controller.member;

import com.sparta.shop_sparta.domain.dto.member.LoginResponseDto;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberControllerImpl implements MemberController{
    @Override
    @PostMapping("/")
    public MemberDto createAccount(@RequestBody MemberDto memberDTO) {
        return memberDTO;
    }

    @Override
    public LoginResponseDto login(MemberDto memberDTO) {
        return null;
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
