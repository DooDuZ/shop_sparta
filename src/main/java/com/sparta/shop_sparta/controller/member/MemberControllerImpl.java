package com.sparta.shop_sparta.controller.member;

import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.dto.member.MemberRequestVo;
import com.sparta.shop_sparta.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
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
    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody MemberDto memberDto, BindingResult bindingResult) {
        System.out.println(memberDto);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        return memberService.createAccount(memberDto);
    }

    @Override
    @GetMapping("/verification")
    public ResponseEntity<?> verifySignup(@RequestParam("memberId") Long memberId, @RequestParam("verificationCode") String verificationCode) {
        return memberService.verifySignup(memberId, verificationCode);
    }

    @Override
    @GetMapping
    public ResponseEntity<?> getMemberInfo(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Long memberId) {
        return memberService.getMemberInfo(userDetails, memberId);
    }

    @Override
    @PatchMapping("/password")
    public ResponseEntity<?> updatePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody MemberRequestVo passwordUpdateRequestDto) {
        return memberService.updatePassword(userDetails, passwordUpdateRequestDto);
    }

    @Override
    @PatchMapping("/phoneNumber")
    public ResponseEntity<?> updatePhoneNumber(@AuthenticationPrincipal UserDetails userDetails, @RequestBody MemberRequestVo phoneNumberUpdateRequestDto) {
        System.out.println(phoneNumberUpdateRequestDto.getPhoneNumber());
        return memberService.updatePhoneNumber(userDetails, phoneNumberUpdateRequestDto);
    }
}
