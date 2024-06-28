package com.sparta.shop_sparta.controller.member.auth;

import com.sparta.shop_sparta.service.member.auth.JwtAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        return jwtAuthService.refreshAccessToken(request, response);
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        return jwtAuthService.logout(request, response);
    }
}
