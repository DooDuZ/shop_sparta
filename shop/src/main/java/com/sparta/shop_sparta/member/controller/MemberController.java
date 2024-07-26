package com.sparta.shop_sparta.member.controller;

import com.sparta.shop_sparta.member.domain.dto.MemberDto;
import com.sparta.shop_sparta.member.domain.dto.MemberRequestVo;
import com.sparta.shop_sparta.member.domain.dto.MemberResponseDto;
import com.sparta.shop_sparta.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody MemberDto memberDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        memberService.createAccount(memberDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/verification")
    public ResponseEntity<Void> verifySignup(@RequestParam("member-id") Long memberId, @RequestParam("verification-code") String verificationCode) {
        memberService.verifySignup(memberId, verificationCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<MemberResponseDto> getMemberInfo(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("member-id") Long memberId) {
        return ResponseEntity.ok(memberService.getMemberInfo(userDetails, memberId));
    }

    @PatchMapping("/{member-id}/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MemberRequestVo passwordUpdateRequestDto,
            @PathVariable("member-id") Long memberId)
    {
        memberService.updatePassword(userDetails, passwordUpdateRequestDto, memberId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{member-id}/phone-number")
    public ResponseEntity<Void> updatePhoneNumber(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MemberRequestVo phoneNumberUpdateRequestDto,
            @PathVariable("member-id") Long memberId) {
        memberService.updatePhoneNumber(userDetails, phoneNumberUpdateRequestDto, memberId);
        return ResponseEntity.ok().build();
    }
}
