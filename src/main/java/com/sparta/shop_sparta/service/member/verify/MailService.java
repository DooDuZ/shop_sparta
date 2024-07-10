package com.sparta.shop_sparta.service.member.verify;

import com.sparta.shop_sparta.config.MailConfig;
import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.exception.MemberException;
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

    private final MailConfig mailConfig;
    private final JavaMailSender javaMailSender;
    private final SignupVerifyCodeRedisRepository signupVerifyCodeRedisRepository;

    @Override
    public void verifySignup(Long memberId, String mailCode) {
        String key = String.valueOf(memberId);
        String verificationCode = (String) signupVerifyCodeRedisRepository.find(key);

        if (!verificationCode.equals(mailCode)) {
            throw new MemberException(MemberResponseMessage.UNMATCHED_VERIFICATION_CODE.getMessage());
        }

        // 사용한 인증 코드 삭제
        signupVerifyCodeRedisRepository.deleteKey(key);
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
                verificationCode);

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
            mimeMessageHelper.setFrom(mailConfig.from, mailConfig.domainName);
            mimeMessageHelper.setTo(memberDto.getEmail());
            mimeMessageHelper.setSubject(mailConfig.mailTitle);
            mimeMessageHelper.setText(message, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
