package com.sparta.shop_sparta.domain.dto.member;

import com.sparta.shop_sparta.constant.member.MemberRole;
import java.util.List;
import lombok.Getter;

@Getter
public class MemberResponseDto {
    private Long memberId;
    private String loginId;
    private String memberName;
    private String phoneNumber;
    private String email;
    private MemberRole role;
    private List<AddrDto> addrDtoList;

    public MemberResponseDto(MemberDto memberDto) {
        this.memberId = memberDto.getMemberId();
        this.loginId = memberDto.getLoginId();
        this.memberName = memberDto.getMemberName();
        this.phoneNumber = memberDto.getPhoneNumber();
        this.email = memberDto.getEmail();
        this.role = memberDto.getRole();
    }

    public MemberResponseDto(MemberDto memberDto, List<AddrDto> addrDtoList) {
        this.memberId = memberDto.getMemberId();
        this.loginId = memberDto.getLoginId();
        this.memberName = memberDto.getMemberName();
        this.phoneNumber = memberDto.getPhoneNumber();
        this.email = memberDto.getEmail();
        this.role = memberDto.getRole();
        this.addrDtoList = addrDtoList;
    }

    public void setAddrDtoList(List<AddrDto> addrDtoList) {
        this.addrDtoList = addrDtoList;
    }
}
