package com.sparta.shop_sparta.domain.dto.member;

import com.sparta.shop_sparta.constant.member.MemberRole;
import lombok.Getter;

@Getter
public class MemberResponseDto {
    private Long memberId;
    private String loginId;
    private String memberName;
    private String phoneNumber;
    private String email;
    private String addr;
    private String addrDetail;
    private MemberRole role;

    public MemberResponseDto(MemberDto memberDto) {
        this.memberId = memberDto.getMemberId();
        this.loginId = memberDto.getLoginId();
        this.memberName = memberDto.getMemberName();
        this.phoneNumber = memberDto.getPhoneNumber();
        this.email = memberDto.getEmail();
        this.addr = memberDto.getAddr();
        this.addrDetail = memberDto.getAddrDetail();
        this.role = memberDto.getRole();
    }
}
