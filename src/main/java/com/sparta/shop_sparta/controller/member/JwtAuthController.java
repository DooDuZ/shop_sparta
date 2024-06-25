package com.sparta.shop_sparta.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface JwtAuthController {
    ResponseEntity<?> refreshAccessToken(String token, HttpServletResponse response);
    ResponseEntity<?> logout(String refreshToken, HttpServletResponse response);
}
