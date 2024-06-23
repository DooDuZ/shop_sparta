package com.sparta.shop_sparta.service.member;

import com.sparta.shop_sparta.config.MailConfig;
import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import com.sparta.shop_sparta.constant.member.MemberRole;
import com.sparta.shop_sparta.constant.redis.RedisPrefix;
import com.sparta.shop_sparta.domain.dto.member.LoginResponseDto;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.exception.CreateAccountException;
import com.sparta.shop_sparta.repository.MemberRepository;
import com.sparta.shop_sparta.repository.RedisRepository;
import com.sparta.shop_sparta.util.encoder.SaltGenerator;
import com.sparta.shop_sparta.util.encoder.UserInformationEncoder;
import com.sparta.shop_sparta.validator.member.EntityFieldValidator;
import com.sparta.shop_sparta.validator.member.pattern.MemberInfoValidator;
import com.sparta.shop_sparta.validator.member.pattern.PatternConfig;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender javaMailSender;
    private final RedisRepository redisRepository;
    private final MailConfig mailConfig;
    private final UserInformationEncoder userInformationEncoder;
    private final SaltGenerator saltGenerator;
    private final AddrService addrService;

    @Override
    @Transactional
    public MemberDto createAccount(MemberDto memberDto) {
        // 가입 정보 유효성 검사
        validateSignupRequest(memberDto);

        // 유저 정보 암호화
        encryptMemberDto(memberDto);

        MemberEntity memberEntity = memberRepository.save(memberDto.toEntity());
        memberEntity.setRole(MemberRole.UNVERIFIED);

        sendVerification(memberEntity);

        return memberEntity.toDto();
    }

    private void encryptMemberDto(MemberDto memberDto) {
        String salt = saltGenerator.generateSalt();
        // 복호화를 위해 salt와 구분자 - 를 합쳐서 저장
        memberDto.setEmail(userInformationEncoder.encrypt(memberDto.getEmail(), salt));
        memberDto.setPhoneNumber(userInformationEncoder.encrypt(memberDto.getPhoneNumber(), salt));
        memberDto.setMemberName(userInformationEncoder.encrypt(memberDto.getMemberName(), salt));
        memberDto.setPassword(bCryptPasswordEncoder.encode(memberDto.getPassword()));
    }

    @Override
    @Transactional
    public Boolean verifySignup(Long memberId, String verificationCode) {
        String key = RedisPrefix.SIGNUP_VERIFICATION.getMessage() + String.valueOf(memberId);
        String verificate = (String) redisRepository.find(key);

        if (!verificate.equals(verificationCode)) {
            throw new CreateAccountException(MemberResponseMessage.UNMATCHED_VERIFICATION_CODE.getMessage());
        }

        MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(
                () -> new CreateAccountException(MemberResponseMessage.NOT_FOUND.getMessage())
        );

        memberEntity.setRole(MemberRole.BASIC);

        // 사용한 인증 코드 삭제
        redisRepository.delete(key);

        return true;
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

    private String getVerificationMessage(MemberEntity memberEntity) {
        StringBuilder verificationMessage = new StringBuilder();

        String verificationCode = getVerificationCode();

        // 레디스에 코드 저장
        // 3분 후 만료
        redisRepository.saveWithDuration(RedisPrefix.SIGNUP_VERIFICATION.getMessage() + memberEntity.getMemberId(),
                verificationCode, 3);

        verificationMessage.append("<h3>")
                .append("<a href=\"").append(mailConfig.requestUrl).append("/member/verification?memberId=")
                .append(memberEntity.getMemberId()).append("&verificationCode=").append(verificationCode)
                .append("\">Click!!</a>")
                .append("</h3>");

        return verificationMessage.toString();
    }

    // 가입 인증 메일 발송
    private void sendVerification(MemberEntity memberEntity) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            String message = getVerificationMessage(memberEntity);

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom(mailConfig.from, mailConfig.DOMAIN_NAME);
            mimeMessageHelper.setTo(userInformationEncoder.decrypt(memberEntity.getEmail()));
            mimeMessageHelper.setSubject(mailConfig.MAIL_TITLE);
            mimeMessageHelper.setText(message, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateSignupRequest(MemberDto memberDto) {

        // 필수 파라미터 검사
        if (!new EntityFieldValidator().validateParams(memberDto.toEntity())) {
            throw new CreateAccountException(MemberResponseMessage.MISSING_REQUIRED_FIELD.getMessage());
        }

        // 아이디 정규식 검사
        if (!new MemberInfoValidator(PatternConfig.loginIdPattern).checkPattern(memberDto.getLoginId())) {
            throw new CreateAccountException(MemberResponseMessage.UNMATCHED_ID.getMessage());
        }

        // 이메일 정규식 검사
        if (!new MemberInfoValidator(PatternConfig.emailPattern).checkPattern(memberDto.getEmail())) {
            throw new CreateAccountException(MemberResponseMessage.UNMATCHED_EMAIL.getMessage());
        }

        // 패스워드 정규식 검사
        if (!new MemberInfoValidator(PatternConfig.passwordPattern).checkPattern(memberDto.getPassword())) {
            throw new CreateAccountException(MemberResponseMessage.UNMATCHED_PASSWORD.getMessage());
        }
        // 휴대폰 번호 정규식 검사
        if (!new MemberInfoValidator(PatternConfig.phoneNumberPattern).checkPattern(memberDto.getPhoneNumber())) {
            throw new CreateAccountException(MemberResponseMessage.UNMATCHED_PHONENUMBER.getMessage());
        }

        // 아이디 중복 검사
        if (memberRepository.findByLoginId(memberDto.getLoginId()).isPresent()) {
            throw new CreateAccountException(MemberResponseMessage.DUPLICATED_LOGIN_ID.getMessage());
        }
        // 이메일 중복 검사
        if (memberRepository.findByEmail(memberDto.getEmail()).isPresent()) {
            throw new CreateAccountException(MemberResponseMessage.DUPLICATED_EMAIL.getMessage());
        }
    }

    @Override
    public LoginResponseDto login(MemberDto memberDTO) {
        return null;
    }

    @Override
    public void logout() {

    }

    @Override
    public void updatePassword(String currentPassword, String newPassword) {

    }

    @Override
    public void updatePhoneNumber(String PhoneNumber) {

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
