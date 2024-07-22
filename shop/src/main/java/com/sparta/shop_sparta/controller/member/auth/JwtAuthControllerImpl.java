package com.sparta.shop_sparta.controller.member.auth;

import com.sparta.shop_sparta.service.member.auth.JwtAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<Void> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        jwtAuthService.refreshAccessToken(request, response);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request, HttpServletResponse response) {
        jwtAuthService.logout(userDetails, request, response);
        return ResponseEntity.ok().build();
    }
}
