package com.sparta.shop_sparta.service.member;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sparta.common.constant.member.MemberResponseMessage;
import com.sparta.common.exception.MemberException;
import com.sparta.shop_sparta.config.MailConfig;
import com.sparta.shop_sparta.repository.memoryRepository.SignupVerifyCodeRedisRepository;
import com.sparta.shop_sparta.service.member.verify.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {
    @InjectMocks
    MailService mailService;
    @Mock
    private MailConfig mailConfig;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private SignupVerifyCodeRedisRepository signupVerifyCodeRedisRepository;

    @Test
    @DisplayName("인증 코드 검증 성공 테스트")
    void verifyCodeSuccessTest(){
        // given
        String verificationCode = "verified!";
        String mailCode = "verified!";
        Long memberId = 1L;

        when(signupVerifyCodeRedisRepository.find(String.valueOf(memberId))).thenReturn(verificationCode);

        // then
        assertThatCode(
                () -> mailService.verifySignup(memberId, mailCode)
        ).doesNotThrowAnyException();

        verify(signupVerifyCodeRedisRepository, times(1)).deleteKey(String.valueOf(memberId));
    }

    @Test
    @DisplayName("인증 코드 검증 실패 테스트")
    void verifyCodeFailTest(){
        // given
        String verificationCode = "verified!";
        String mailCode = "verified?";
        Long memberId = 1L;

        when(signupVerifyCodeRedisRepository.find(String.valueOf(memberId))).thenReturn(verificationCode);

        // then
        assertThatThrownBy(
                () -> mailService.verifySignup(memberId, mailCode)
        ).isInstanceOf(MemberException.class).hasMessage(MemberResponseMessage.UNMATCHED_VERIFICATION_CODE.getMessage());
    }
}
