package com.sparta.shop_sparta.service.member.auth;

import com.sparta.shop_sparta.config.security.jwt.JwtTokenProvider;
import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import com.sparta.shop_sparta.repository.memoryRepository.JwtRedisRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtAuthServiceImpl implements JwtAuthService {

    private final JwtRedisRepository jwtRedisRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 단순 상수들... 나중에 클래스 분리
    private final String accessTokenCookieName = "access_token";
    private final String refreshTokenCookieName = "refresh_token";

    private final int accessTokenExpirySecond = 60 * 15;
    private final int refreshTokenExpirySecond = 60 * 60 * 24 * 7;
    //

    // controller가 아닌 usernamePasswordAuthenticationFilter -> loginSuccessHandler 에서 접근
    @Override
    public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response,
                                   Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        generateTokens(username, role, response);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> logout(String refreshToken, HttpServletResponse response) {
        String key = jwtTokenProvider.getUsername(refreshToken);
        jwtRedisRepository.delete(key);

        setCookie(response, accessTokenCookieName, null, 0);
        return ResponseEntity.ok().build();
    }

    // 응답을 쿠키를 response에 직접 담았는데, reponseEntity를 사용해서 할까 고민중
    // 일단 이렇게 넘기기
    @Override
    public ResponseEntity<?> refreshAccessToken(String token, HttpServletResponse response) {
        System.out.println(token);
        String username = jwtTokenProvider.getUsername(token);
        String role = jwtTokenProvider.getRole(token);

        // 리프레시 토큰이 없거나, 만료되었으면
        if (!validateExpireDate(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MemberResponseMessage.INVALID_TOKEN);
            //throw new MemberAuthorizeException(MemberResponseMessage.INVALID_TOKEN.getMessage());
        }

        generateTokens(username, role, response);

        return ResponseEntity.ok().build();
    }

    private void generateTokens(String username, String role, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.createAccessToken(username, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(username, role);

        // 인증 됐기 때문에 저장된 리프레시 토큰도 현재 시점으로 갱신해준다.
        jwtRedisRepository.saveWithDuration(username, refreshToken, refreshTokenExpirySecond / 60);

        setCookie(response, accessTokenCookieName, accessToken, accessTokenExpirySecond);
        setCookie(response, refreshTokenCookieName, refreshToken, refreshTokenExpirySecond);
    }

    @Override
    public Boolean validateExpireDate(String token) {
        String username = jwtTokenProvider.getUsername(token);

        // 레디스에 저장된 리프레시 토큰 가져오기
        String refreshToken = (String) jwtRedisRepository.find(username);

        // 리프레시 토큰이 없거나, 만료되었으면
        if (refreshToken == null || jwtTokenProvider.getExpireDate(refreshToken).before(new Date())) {
            return false;
        }

        return true;
    }

    private void setCookie(HttpServletResponse response, String name, String token, int maxAge) {
        Cookie accessCookie = new Cookie(name, token);
        accessCookie.setHttpOnly(true);
        //cookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(maxAge);

        response.addCookie(accessCookie);
    }
}