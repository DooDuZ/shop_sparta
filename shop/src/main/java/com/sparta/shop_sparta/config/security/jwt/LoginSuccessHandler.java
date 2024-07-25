package com.sparta.shop_sparta.config.security.jwt;

import com.sparta.shop_sparta.member.service.auth.JwtAuthServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtAuthServiceImpl jwtAuthService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        jwtAuthService.login(request, response, authentication);
        // super.onAuthenticationSuccess를 호출하면 "/"로 리다이렉트
        clearAuthenticationAttributes(request);
    }
}
