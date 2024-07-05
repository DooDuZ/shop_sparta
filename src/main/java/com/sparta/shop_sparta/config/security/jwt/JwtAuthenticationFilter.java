package com.sparta.shop_sparta.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import com.sparta.shop_sparta.service.member.auth.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);
        // 서명 확인
        Jws<Claims> claims = jwtTokenProvider.validateToken(token);

        if (token != null && claims != null) {

            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }else if(token != null && claims == null){
            // 리프레시 토큰으로 재요청 하라고 전달
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            ObjectMapper objectMapper = new ObjectMapper();
            String errorMessage = AuthMessage.INVALID_TOKEN.getMessage();

            response.getWriter().write(objectMapper.writeValueAsString(errorMessage));
        }

        filterChain.doFilter(request, response);
    }
}
