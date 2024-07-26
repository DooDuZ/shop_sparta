package com.sparta.shop_sparta.util.test;

import com.sparta.common.constant.member.MemberRole;
import com.sparta.shop_sparta.member.service.auth.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestTokenService {

    private final JwtTokenProvider jwtTokenProvider;

    private final String accessTokenCookieName = "access_token";
    private final int accessTokenExpirySecond = 60 * 30;

    public void getToken(UserInfo userInfo, HttpServletResponse response){
        String accessToken = jwtTokenProvider.createAccessToken(userInfo.getUsername(), MemberRole.BASIC.getGrade(), String.valueOf(userInfo.getMemberId()));
        setCookie(response, accessTokenCookieName, accessToken, accessTokenExpirySecond);
    }

    private void setCookie(HttpServletResponse response, String name, String token, int maxAge) {
        Cookie accessCookie = new Cookie(name, token);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(maxAge);

        response.addCookie(accessCookie);
    }
}
