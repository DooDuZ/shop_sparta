package com.sparta.shop_sparta.service.member.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import com.sparta.shop_sparta.domain.dto.member.token.TokenWrapper;
import com.sparta.shop_sparta.exception.member.MemberException;
import com.sparta.shop_sparta.repository.memoryRepository.JwtRedisRepository;
import com.sparta.shop_sparta.util.encoder.TokenUsernameEncoder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    // private final String refreshTokenCookieName = "refresh_token";

    private final int accessTokenExpirySecond = 60 * 15;
    private final int refreshTokenExpirySecond = 60 * 60 * 24 * 7;
    private final TokenUsernameEncoder tokenUsernameEncoder;
    //

    // controller가 아닌 usernamePasswordAuthenticationFilter -> loginSuccessHandler 에서 접근
    @Override
    public void login(HttpServletRequest request, HttpServletResponse response,
                                   Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        TokenWrapper tokenWrapper = generateTokens(username, role, request, response);

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(tokenWrapper));
        }catch (Exception e){
            throw new MemberException(MemberResponseMessage.FAIL_CONVERT_TO_JSON.getMessage(), e);
        }
    }

    @Override
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        System.out.println(refreshToken);

        String key = jwtTokenProvider.getUsernameByRefresh(refreshToken);
        String userAgent = jwtTokenProvider.getUserAgentByRefresh(refreshToken);
        jwtRedisRepository.deleteUserAgent(key, userAgent);

        setCookie(response, accessTokenCookieName, null, 0);
        return ResponseEntity.ok().build();
    }


    @Override
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        // 리프레시 토큰이 유효하지 않거나, 레디스에 존재하지 않으면
        if (jwtTokenProvider.validateRefreshToken(refreshToken) == null || !isManaged(refreshToken)) {
            throw new MemberException(MemberResponseMessage.INVALID_TOKEN.getMessage());
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MemberResponseMessage.INVALID_TOKEN);
        }

        String username = jwtTokenProvider.getUsernameByRefresh(refreshToken);
        String role = jwtTokenProvider.getRoleByRefresh(refreshToken);

        // 액세스 토큰만 발급으로 변경
        try {
            String accessToken = jwtTokenProvider.createAccessToken(tokenUsernameEncoder.decrypt(username),role);
            setCookie(response, accessTokenCookieName, accessToken, accessTokenExpirySecond);
        }catch (Exception e){
            throw new MemberException(e);
        }

        return ResponseEntity.ok().build();
    }

    private TokenWrapper generateTokens(String username, String role, HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.createAccessToken(username, role);
        String userAgent = request.getHeader("User-Agent");

        String refreshToken = jwtTokenProvider.createRefreshToken(username, role, userAgent);

        // 발급한 refreshToken을 redis에 등록
        // 유저 이름 암호화 되어있으므로 provider에서 뽑아서 써야한다
        jwtRedisRepository.save(jwtTokenProvider.getUsernameByRefresh(refreshToken).toString(), userAgent);

        setCookie(response, accessTokenCookieName, accessToken, accessTokenExpirySecond);

        return new TokenWrapper(refreshToken);
    }

    public Boolean isManaged(String token) {
        String username = jwtTokenProvider.getUsernameByRefresh(token);
        String userAgent = jwtTokenProvider.getUserAgentByRefresh(token);

        // 레디스에 저장된 리프레시 토큰 가져오기
        return jwtRedisRepository.findUserAgent(username, userAgent);
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