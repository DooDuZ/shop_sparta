package com.sparta.shop_sparta.service.member.verify;

import com.sparta.shop_sparta.config.MailConfig;
import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.exception.member.MemberException;
import com.sparta.shop_sparta.repository.memoryRepository.SignupVerifyCodeRedisRepository;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService implements VerifySignUpService<MemberDto>{
    // 메일 유틸 -> 다른 서비스로 분리
    private final MailConfig mailConfig;
    private final JavaMailSender javaMailSender;
    private final SignupVerifyCodeRedisRepository signupVerifyCodeRedisRepository;

    @Override
    public void verifySignup(Long memberId, String verificationCode) {
        String key = String.valueOf(memberId);
        String verificate = (String) signupVerifyCodeRedisRepository.find(key);

        if (!verificate.equals(verificationCode)) {
            throw new MemberException(MemberResponseMessage.UNMATCHED_VERIFICATION_CODE.getMessage());
        }

        // 사용한 인증 코드 삭제
        signupVerifyCodeRedisRepository.delete(key);
    }

    private String getVerificationCode() {
        StringBuilder code = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < 6; i++) {
            int number = secureRandom.nextInt(10);
            code.append(number);
        }

        return code.toString();
    }

    private String getVerificationMessage(MemberDto memberDto) {
        StringBuilder verificationMessage = new StringBuilder();

        String verificationCode = getVerificationCode();

        // 레디스에 코드 저장
        // 3분 후 만료
        signupVerifyCodeRedisRepository.saveWithDuration(String.valueOf(memberDto.getMemberId()),
                verificationCode, 3);

        System.out.println(signupVerifyCodeRedisRepository.find(String.valueOf(memberDto.getMemberId())));

        verificationMessage.append("<h3>")
                .append("<a href=\"").append(mailConfig.requestUrl).append("/member/verification?memberId=")
                .append(memberDto.getMemberId()).append("&verificationCode=").append(verificationCode)
                .append("\">Click!!</a>")
                .append("</h3>");

        return verificationMessage.toString();
    }

    // 가입 인증 메일 발송
    @Override
    public void sendVerification(MemberDto memberDto) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            String message = getVerificationMessage(memberDto);

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom(mailConfig.from, mailConfig.DOMAIN_NAME);
            mimeMessageHelper.setTo(memberDto.getEmail());
            mimeMessageHelper.setSubject(mailConfig.MAIL_TITLE);
            mimeMessageHelper.setText(message, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
