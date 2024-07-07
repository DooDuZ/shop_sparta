package com.sparta.shop_sparta.controller.member;

import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.dto.member.MemberRequestVo;
import com.sparta.shop_sparta.domain.dto.member.MemberResponseDto;
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
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        memberService.createAccount(memberDto);

        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/verification")
    public ResponseEntity<?> verifySignup(@RequestParam("memberId") Long memberId, @RequestParam("verificationCode") String verificationCode) {
        memberService.verifySignup(memberId, verificationCode);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping
    public ResponseEntity<MemberResponseDto> getMemberInfo(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Long memberId) {
        return ResponseEntity.ok(memberService.getMemberInfo(userDetails, memberId));
    }

    @Override
    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody MemberRequestVo passwordUpdateRequestDto) {
        return ResponseEntity.ok().build();
    }

    @Override
    @PatchMapping("/phoneNumber")
    public ResponseEntity<Void> updatePhoneNumber(@AuthenticationPrincipal UserDetails userDetails, @RequestBody MemberRequestVo phoneNumberUpdateRequestDto) {
        return ResponseEntity.ok().build();
    }
}
