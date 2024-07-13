package com.sparta.shop_sparta.service.member.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import com.sparta.shop_sparta.domain.dto.member.token.TokenWrapper;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.exception.AuthorizationException;
import com.sparta.shop_sparta.exception.MemberException;
import com.sparta.shop_sparta.repository.memoryRepository.JwtRedisRepository;
import com.sparta.shop_sparta.util.encoder.TokenUsernameEncoder;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final String USER_AGENT_KEY = "User-Agent";
    //

    // controller가 아닌 usernamePasswordAuthenticationFilter -> loginSuccessHandler 에서 접근
    @Override
    public void login(HttpServletRequest request, HttpServletResponse response,
                      Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        MemberEntity memberEntity = (MemberEntity) authentication.getPrincipal();

        Long memberId = memberEntity.getMemberId();

        TokenWrapper tokenWrapper = generateTokens(username, role, String.valueOf(memberId), request, response);

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(tokenWrapper));
        } catch (Exception e) {
            throw new MemberException(MemberResponseMessage.FAIL_CONVERT_TO_JSON, e);
        }
    }

    @Override
    public void logout(UserDetails userDetails, HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.resolveToken(request);

        try {
            // 엑세스 토큰에서 유저 이름 추출 -> redis key로 저장된 형식으로 인코딩
            String key = tokenUsernameEncoder.encrypt(jwtTokenProvider.getUsername(accessToken));
            String userAgent = request.getHeader(USER_AGENT_KEY);
            jwtRedisRepository.deleteUserAgent(key, userAgent);

            setCookie(response, accessTokenCookieName, null, 0);
        } catch (Exception e) {
            throw new AuthorizationException(AuthMessage.INVALID_TOKEN, e);
        }
    }


    @Override
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        // 리프레시 토큰이 유효하지 않거나, 레디스에 존재하지 않으면
        if (jwtTokenProvider.validateRefreshToken(refreshToken) == null || !isManaged(refreshToken)) {
            throw new AuthorizationException(AuthMessage.INVALID_TOKEN);
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MemberResponseMessage.INVALID_TOKEN);
        }

        String username = jwtTokenProvider.getUsernameByRefresh(refreshToken);
        String role = jwtTokenProvider.getRoleByRefresh(refreshToken);
        String memberId = jwtTokenProvider.getRefreshMemberId(refreshToken);

        // 액세스 토큰만 발급으로 변경
        try {
            String accessToken = jwtTokenProvider.createAccessToken(tokenUsernameEncoder.decrypt(username), role, memberId);
            setCookie(response, accessTokenCookieName, accessToken, accessTokenExpirySecond);
        } catch (Exception e) {
            throw new MemberException(MemberResponseMessage.FAIL_CONVERT_TO_JSON);
        }
    }

    private TokenWrapper generateTokens(String username, String role, String memberId, HttpServletRequest request,
                                        HttpServletResponse response) {
        String accessToken = jwtTokenProvider.createAccessToken(username, role, memberId);
        String userAgent = request.getHeader(USER_AGENT_KEY);

        String refreshToken = jwtTokenProvider.createRefreshToken(username, role, memberId, userAgent);

        // 발급한 refreshToken을 redis에 등록
        // 유저 이름 암호화 되어있으므로 provider에서 뽑아서 써야한다
        jwtRedisRepository.saveWithDuration(jwtTokenProvider.getUsernameByRefresh(refreshToken).toString(), userAgent,
                refreshToken);

        setCookie(response, accessTokenCookieName, accessToken, accessTokenExpirySecond);

        return new TokenWrapper(refreshToken);
    }

    public Boolean isManaged(String token) {
        String username = jwtTokenProvider.getUsernameByRefresh(token);
        String userAgent = jwtTokenProvider.getUserAgentByRefresh(token);

        // 레디스에 저장된 리프레시 토큰 가져오기
        return jwtRedisRepository.findUserAgent(username, userAgent).equals(token);
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