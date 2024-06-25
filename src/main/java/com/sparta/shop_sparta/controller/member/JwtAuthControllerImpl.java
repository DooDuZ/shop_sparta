package com.sparta.shop_sparta.controller.member;

import com.sparta.shop_sparta.service.member.auth.JwtAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class JwtAuthControllerImpl implements JwtAuthController{

    private final JwtAuthService jwtAuthService;

    @Override
    @PostMapping("/token")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(name="refresh_token") String token, HttpServletResponse response) {
        return jwtAuthService.refreshAccessToken(token, response);
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refresh_token") String refreshToken, HttpServletResponse response) {
        return jwtAuthService.logout(refreshToken, response);
    }
}
