package com.sparta.shop_sparta.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import com.sparta.shop_sparta.service.member.auth.JwtAuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
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

        // 토큰값이 존재하고, 현재 엑세스 토큰이 유효하며, 리프레시 토큰의 유효기간이 지나지 않은 경우
        // 로그인 할 때마다 해당 유저의 리프레시 토큰을 찾아서 유효기간을 조회한다 -> 이럼 엑세스 토큰 왜 쓰지... 사실상 세션인데 하지말자...
        // 그렇다면 로그아웃 된 뒤의 접근은 어떻게 막나? 블랙리스트? 접근할 때마다 레디스에서 값을 찾아? 세션이랑 뭐가다름...?
        if (token != null && claims != null) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }else if(token != null && claims == null){
            // 리프레시 토큰으로 재요청 하라고 전달
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            ObjectMapper objectMapper = new ObjectMapper();
            String errorMessage = MemberResponseMessage.INVALID_TOKEN.getMessage();

            response.getWriter().write(objectMapper.writeValueAsString(errorMessage));
        }

        filterChain.doFilter(request, response);
    }
}
