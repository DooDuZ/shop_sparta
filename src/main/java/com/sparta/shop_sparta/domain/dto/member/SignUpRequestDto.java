package com.sparta.shop_sparta.domain.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequestDto {
    private String loginId;
    private String password;
    private String addr;
    private String addrDetail;
    private String PhoneNumber;
}
