package com.sparta.shop_sparta.service.member.auth;

import com.sparta.shop_sparta.repository.memoryRepository.JwtRedisRepository;
import com.sparta.shop_sparta.util.encoder.TokenUsernameEncoder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JwtAuthServiceTest {

    @InjectMocks
    JwtAuthServiceImpl jwtAuthService;
    @Mock
    private JwtRedisRepository jwtRedisRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    // 단순 상수들... 나중에 클래스 분리
    private final String accessTokenCookieName = "access_token";
    // private final String refreshTokenCookieName = "refresh_token";

    private final int accessTokenExpirySecond = 60 * 15;
    private final int refreshTokenExpirySecond = 60 * 60 * 24 * 7;
    private final String USER_AGENT_KEY = "User-Agent";

    @Mock
    private TokenUsernameEncoder tokenUsernameEncoder;


}
