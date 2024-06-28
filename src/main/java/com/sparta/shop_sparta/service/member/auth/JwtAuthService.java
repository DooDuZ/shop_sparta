package com.sparta.shop_sparta.service.member.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtAuthService {
    void login(HttpServletRequest req, HttpServletResponse res, Authentication authentication);
    ResponseEntity<?> logout(UserDetails userDetails, HttpServletRequest request, HttpServletResponse res);
    ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response);
}
