package com.sparta.shop_sparta.service.member;

import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import com.sparta.shop_sparta.constant.member.MemberRole;
import com.sparta.shop_sparta.domain.dto.member.AddrDto;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.dto.member.MemberResponseDto;
import com.sparta.shop_sparta.domain.dto.member.MemberRequestVo;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.exception.MemberException;
import com.sparta.shop_sparta.repository.MemberRepository;
import com.sparta.shop_sparta.service.member.addr.AddrService;
import com.sparta.shop_sparta.service.member.verify.MailService;
import com.sparta.shop_sparta.util.encoder.SaltGenerator;
import com.sparta.shop_sparta.util.encoder.UserInformationEncoder;
import com.sparta.shop_sparta.validator.member.EntityFieldValidator;
import com.sparta.shop_sparta.validator.member.pattern.MemberInfoValidator;
import com.sparta.shop_sparta.validator.member.pattern.PatternConfig;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final AddrService addrService;

    // 복호화 가능한 인코더
    private final SaltGenerator saltGenerator;
    private final UserInformationEncoder userInformationEncoder;

    // 복호화 불가능
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ResponseEntity<?> createAccount(MemberDto memberDto) {
        // 가입 정보 유효성 검사
        validateSignupRequest(memberDto);

        // 유저 정보 암호화
        encryptMemberDto(memberDto);

        MemberEntity memberEntity = memberRepository.save(memberDto.toEntity());
        memberEntity.setRole(MemberRole.UNVERIFIED);

        memberDto.setMemberId(memberEntity.getMemberId());

        // 다른 서비스 처리를 위해 복호화 후 회원번호 set
        decryptMemberDto(memberDto);
        mailService.sendVerification(memberDto);

        // 주소 서비스 통해서 주소 저장
        addrService.addAddr(memberEntity,
                AddrDto.builder().addr(memberDto.getAddr()).addrDetail(memberDto.getAddrDetail()).build());

        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<?> verifySignup(Long memberId, String verificationCode) {
        // 메일 서비스를 사용한 검증
        // 코드 인즈 안되면 exception 발생
        mailService.verifySignup(memberId, verificationCode);

        MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MemberResponseMessage.NOT_FOUND.getMessage())
        );

        memberEntity.setRole(MemberRole.BASIC);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> getMemberInfo(UserDetails userDetails, Long memberId) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        if (memberEntity.getMemberId() != memberId) {
            throw new MemberException(AuthMessage.INVALID_PRINCIPLE.getMessage());
        }

        MemberDto memberDto = memberEntity.toDto();
        //decryptMemberDto(memberDto);
        memberDto.setPassword("");

        // 주소 목록 추가
        List<AddrDto> addrDtoList = addrService.getAddrList(memberEntity.getMemberId());

        return ResponseEntity.ok(new MemberResponseDto(memberDto, addrDtoList));
    }

    private void validateSignupRequest(MemberDto memberDto) {
        MemberInfoValidator memberInfoValidator = new MemberInfoValidator();

        // 필수 파라미터 검사
        if (!new EntityFieldValidator().validateParams(memberDto.toEntity())) {
            throw new MemberException(MemberResponseMessage.MISSING_REQUIRED_FIELD.getMessage());
        }

        // 아이디 정규식 검사
        if (!memberInfoValidator.checkPattern(PatternConfig.loginIdPattern, memberDto.getLoginId())) {
            throw new MemberException(MemberResponseMessage.UNMATCHED_ID.getMessage());
        }

        // 이메일 정규식 검사
        if (!memberInfoValidator.checkPattern(PatternConfig.emailPattern, memberDto.getEmail())) {
            throw new MemberException(MemberResponseMessage.UNMATCHED_EMAIL.getMessage());
        }

        // 패스워드 정규식 검사
        if (!memberInfoValidator.checkPattern(PatternConfig.passwordPattern, memberDto.getPassword())) {
            throw new MemberException(MemberResponseMessage.UNMATCHED_PASSWORD.getMessage());
        }
        // 휴대폰 번호 정규식 검사
        if (!memberInfoValidator.checkPattern(PatternConfig.phoneNumberPattern, memberDto.getPhoneNumber())) {
            throw new MemberException(MemberResponseMessage.UNMATCHED_PHONENUMBER.getMessage());
        }

        // 이메일 중복 검사
        if (memberRepository.findByEmail(memberDto.getEmail()).isPresent()) {
            throw new MemberException(MemberResponseMessage.DUPLICATED_EMAIL.getMessage());
        }

        // 아이디 중복 검사
        if (memberRepository.findByLoginId(memberDto.getLoginId()).isPresent()) {
            throw new MemberException(MemberResponseMessage.DUPLICATED_LOGIN_ID.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> updatePassword(UserDetails userDetails, MemberRequestVo passwordRequestDto) {
        if (passwordRequestDto.getPassword() == null || passwordRequestDto.getConfirmPassword() == null) {
            throw new MemberException(MemberResponseMessage.MISSING_REQUIRED_FIELD.getMessage());
        }

        MemberEntity memberEntity = (MemberEntity) userDetails;

        String password = passwordRequestDto.getPassword();
        String confirmPassword = passwordRequestDto.getConfirmPassword();

        if (!passwordEncoder.matches(password, memberEntity.getPassword())) {
            throw new MemberException(MemberResponseMessage.INVALID_PASSWORD.getMessage());
        }

        if (!new MemberInfoValidator().checkPattern(PatternConfig.passwordPattern, confirmPassword)) {
            throw new MemberException(MemberResponseMessage.UNMATCHED_PASSWORD.getMessage());
        }

        passwordEncoder.matches(password, memberEntity.getPassword());

        memberEntity.setPassword(passwordEncoder.encode(confirmPassword));

        return ResponseEntity.ok().build();
    }


    @Override
    @Transactional
    public ResponseEntity<?> updatePhoneNumber(UserDetails userDetails, MemberRequestVo phoneNumberUpdateRequestDto) {
        if (phoneNumberUpdateRequestDto.getPhoneNumber() == null) {
            return ResponseEntity.badRequest().build();
        }

        String phoneNumber = phoneNumberUpdateRequestDto.getPhoneNumber();

        if (!new MemberInfoValidator().checkPattern(PatternConfig.phoneNumberPattern, phoneNumber)) {
            return ResponseEntity.ok(MemberResponseMessage.UNMATCHED_PHONENUMBER.getMessage());
        }

        String salt = saltGenerator.generateSalt();
        MemberEntity memberEntity = (MemberEntity) userDetails;

        memberEntity.setPhoneNumber(userInformationEncoder.encrypt(phoneNumber, salt));

        return ResponseEntity.ok().build();
    }

    private void encryptMemberDto(MemberDto memberDto) {
        String salt = saltGenerator.generateSalt();
        // 복호화를 위해 salt와 구분자 - 를 합쳐서 저장
        memberDto.setEmail(userInformationEncoder.encrypt(memberDto.getEmail(), salt));
        memberDto.setPhoneNumber(userInformationEncoder.encrypt(memberDto.getPhoneNumber(), salt));
        memberDto.setMemberName(userInformationEncoder.encrypt(memberDto.getMemberName(), salt));
        memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
    }

    // userDetails 사용으로 바뀌면서 사용하는 경우 없음
    // 바로 삭제하지 않고 일단 keep
    private void decryptMemberDto(MemberDto memberDto) {
        memberDto.setEmail(userInformationEncoder.decrypt(memberDto.getEmail()));
        memberDto.setPhoneNumber(userInformationEncoder.decrypt(memberDto.getPhoneNumber()));
        memberDto.setMemberName(userInformationEncoder.decrypt(memberDto.getMemberName()));
        memberDto.setPassword("");
    }
}
