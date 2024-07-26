package com.sparta.shop_sparta.member.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class MemberRequestVo {
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "휴대폰 번호 양식을 맞춰주세요.")
    private final String phoneNumber;
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=])(?=.*[0-9]).{10,16}$", message = "영 대/소문자, 숫자, 특수문자 필수 포함 10-16자")
    private final String password;
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=])(?=.*[0-9]).{10,16}$", message = "영 대/소문자, 숫자, 특수문자 필수 포함 10-16자")
    private final String confirmPassword;
    @NotBlank
    private final String addr;
    private final String addrDetail;
}
