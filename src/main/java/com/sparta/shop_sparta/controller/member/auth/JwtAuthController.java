package com.sparta.shop_sparta.controller.member.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface JwtAuthController {
    ResponseEntity<?> refreshAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse response);
    ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response);
}
