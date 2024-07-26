package com.sparta.shop_sparta.member.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtAuthController {
    ResponseEntity<?> refreshAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse response);
    ResponseEntity<?> logout(UserDetails userDetails, HttpServletRequest request, HttpServletResponse response);
}
