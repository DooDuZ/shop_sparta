package com.sparta.shop_sparta.member.domain.dto.token;

import java.io.Serializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class TokenWrapper implements Serializable {
    private final String refreshToken;
}
