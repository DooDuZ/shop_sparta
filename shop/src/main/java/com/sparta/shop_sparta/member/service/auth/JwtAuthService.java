package com.sparta.shop_sparta.member.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtAuthService {
    void login(HttpServletRequest req, HttpServletResponse res, Authentication authentication);
    void logout(UserDetails userDetails, HttpServletRequest request, HttpServletResponse res);
    void refreshAccessToken(HttpServletRequest request, HttpServletResponse response);
}
