package com.sparta.shop_sparta.service.member;

import com.sparta.shop_sparta.config.MailConfig;
import com.sparta.shop_sparta.config.security.jwt.JwtTokenProvider;
import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import com.sparta.shop_sparta.constant.member.MemberRole;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.dto.member.PasswordRequestDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.exception.member.MemberAuthorizeException;
import com.sparta.shop_sparta.repository.MemberRepository;
import com.sparta.shop_sparta.repository.memoryRepository.SignupVerifyCodeRedisRepository;
import com.sparta.shop_sparta.util.encoder.SaltGenerator;
import com.sparta.shop_sparta.util.encoder.UserInformationEncoder;
import com.sparta.shop_sparta.validator.member.EntityFieldValidator;
import com.sparta.shop_sparta.validator.member.pattern.MemberInfoValidator;
import com.sparta.shop_sparta.validator.member.pattern.PasswordValidator;
import com.sparta.shop_sparta.validator.member.pattern.PatternConfig;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final SignupVerifyCodeRedisRepository signupVerifyCodeRedisRepository;

    // JwtProvider
    private final JwtTokenProvider jwtTokenProvider;

    // 메일 유틸 -> 다른 서비스로 분리
    private final MailConfig mailConfig;
    private final JavaMailSender javaMailSender;

    // 복호화 가능한 인코더
    private final SaltGenerator saltGenerator;
    private final UserInformationEncoder userInformationEncoder;

    // 복호화 불가능
    private final PasswordEncoder passwordEncoder;

    private final AddrService addrService;

    @Override
    @Transactional
    public ResponseEntity<?> createAccount(MemberDto memberDto) {
        // 가입 정보 유효성 검사
        validateSignupRequest(memberDto);

        // 유저 정보 암호화
        encryptMemberDto(memberDto);

        MemberEntity memberEntity = memberRepository.save(memberDto.toEntity());
        memberEntity.setRole(MemberRole.UNVERIFIED);

        sendVerification(memberEntity);

        // 주소 서비스 통해서 주소 저장
        addrService.addAddr(memberEntity, memberDto.getAddr(), memberDto.getAddrDetail());

        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<?> verifySignup(Long memberId, String verificationCode) {
        String key = String.valueOf(memberId);
        String verificate = (String) signupVerifyCodeRedisRepository.find(key);

        if (!verificate.equals(verificationCode)) {
            throw new MemberAuthorizeException(MemberResponseMessage.UNMATCHED_VERIFICATION_CODE.getMessage());
        }

        MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberAuthorizeException(MemberResponseMessage.NOT_FOUND.getMessage())
        );

        memberEntity.setRole(MemberRole.BASIC);

        // 사용한 인증 코드 삭제
        signupVerifyCodeRedisRepository.delete(key);

        return ResponseEntity.ok().build();
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
        signupVerifyCodeRedisRepository.saveWithDuration(String.valueOf(memberEntity.getMemberId()),
                verificationCode, 3);

        System.out.println(signupVerifyCodeRedisRepository.find(String.valueOf(memberEntity.getMemberId())));

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
            throw new MemberAuthorizeException(MemberResponseMessage.MISSING_REQUIRED_FIELD.getMessage());
        }

        // 아이디 정규식 검사
        if (!new MemberInfoValidator(PatternConfig.loginIdPattern).checkPattern(memberDto.getLoginId())) {
            throw new MemberAuthorizeException(MemberResponseMessage.UNMATCHED_ID.getMessage());
        }

        // 이메일 정규식 검사
        if (!new MemberInfoValidator(PatternConfig.emailPattern).checkPattern(memberDto.getEmail())) {
            throw new MemberAuthorizeException(MemberResponseMessage.UNMATCHED_EMAIL.getMessage());
        }

        // 패스워드 정규식 검사
        if (!new MemberInfoValidator(PatternConfig.passwordPattern).checkPattern(memberDto.getPassword())) {
            throw new MemberAuthorizeException(MemberResponseMessage.UNMATCHED_PASSWORD.getMessage());
        }
        // 휴대폰 번호 정규식 검사
        if (!new MemberInfoValidator(PatternConfig.phoneNumberPattern).checkPattern(memberDto.getPhoneNumber())) {
            throw new MemberAuthorizeException(MemberResponseMessage.UNMATCHED_PHONENUMBER.getMessage());
        }

        // 이메일 중복 검사
        if (memberRepository.findByEmail(memberDto.getEmail()).isPresent()) {
            throw new MemberAuthorizeException(MemberResponseMessage.DUPLICATED_EMAIL.getMessage());
        }

        // 아이디 중복 검사
        if (memberRepository.findByLoginId(memberDto.getLoginId()).isPresent()) {
            throw new MemberAuthorizeException(MemberResponseMessage.DUPLICATED_LOGIN_ID.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> updatePassword(PasswordRequestDto passwordRequestDto) {
        MemberEntity memberEntity = getMemberEntity();

        String password = passwordRequestDto.getPassword();
        String confirmPassword = passwordRequestDto.getConfirmPassword();

        if (!passwordEncoder.matches(password, memberEntity.getPassword())){
            return ResponseEntity.ok(MemberResponseMessage.INVALID_PASSWORD);
        }

        if (new MemberInfoValidator(PatternConfig.passwordPattern).checkPattern(confirmPassword)){
            return ResponseEntity.ok(MemberResponseMessage.UNMATCHED_PASSWORD);
        }

        passwordEncoder.matches(password, memberEntity.getPassword());

        memberEntity.setPassword(passwordEncoder.encode(confirmPassword));

        return ResponseEntity.ok().build();
    }


    @Override
    public ResponseEntity<?> updatePhoneNumber(String PhoneNumber) {
        return null;
    }

    private void encryptMemberDto(MemberDto memberDto) {
        String salt = saltGenerator.generateSalt();
        // 복호화를 위해 salt와 구분자 - 를 합쳐서 저장
        memberDto.setEmail(userInformationEncoder.encrypt(memberDto.getEmail(), salt));
        memberDto.setPhoneNumber(userInformationEncoder.encrypt(memberDto.getPhoneNumber(), salt));
        memberDto.setMemberName(userInformationEncoder.encrypt(memberDto.getMemberName(), salt));
        memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
    }


    private MemberEntity getMemberEntity(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("[서버 내부 오류] 인증 정보 없음");
        }

        Object principal = authentication.getPrincipal();

        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return memberRepository.findByLoginId(username).orElseThrow(
                () -> new MemberAuthorizeException(MemberResponseMessage.NOT_FOUND.getMessage())
        );
    }
}
