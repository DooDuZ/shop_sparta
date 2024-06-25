package com.sparta.shop_sparta.service.member.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface JwtAuthService {
    ResponseEntity<?> login(HttpServletRequest req, HttpServletResponse res, Authentication authentication);
    ResponseEntity<?> logout(String refreshToken, HttpServletResponse res);
    ResponseEntity<?> refreshAccessToken(String token, HttpServletResponse response);
    Boolean validateExpireDate(String token);

}
