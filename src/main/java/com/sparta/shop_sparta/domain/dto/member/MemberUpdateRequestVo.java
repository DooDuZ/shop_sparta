package com.sparta.shop_sparta.domain.dto.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberUpdateRequestVo {
    private final String phoneNumber;
    private final String password;
    private final String confirmPassword;
    private final String addr;
    private final String addrDetail;
}
