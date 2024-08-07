package com.sparta.shop_sparta.member.service;

import com.sparta.common.constant.member.AuthMessage;
import com.sparta.common.constant.member.MemberResponseMessage;
import com.sparta.common.constant.member.MemberRole;
import com.sparta.common.exception.AuthorizationException;
import com.sparta.common.exception.MemberException;
import com.sparta.shop_sparta.config.PatternConfig;
import com.sparta.shop_sparta.member.domain.dto.AddrDto;
import com.sparta.shop_sparta.member.domain.dto.MemberDto;
import com.sparta.shop_sparta.member.domain.dto.MemberResponseDto;
import com.sparta.shop_sparta.member.domain.dto.MemberRequestVo;
import com.sparta.shop_sparta.member.domain.entity.MemberEntity;
import com.sparta.shop_sparta.member.repository.MemberRepository;
import com.sparta.shop_sparta.member.service.addr.AddrService;
import com.sparta.shop_sparta.member.service.verify.MailService;
import com.sparta.shop_sparta.util.encoder.SaltGenerator;
import com.sparta.shop_sparta.util.encoder.UserInformationEncoder;
import com.sparta.shop_sparta.util.validator.member.pattern.MemberInfoValidator;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final AddrService addrService;

    // 복호화 가능한 인코더
    private final SaltGenerator saltGenerator;
    private final UserInformationEncoder userInformationEncoder;

    // 복호화 불가능
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createAccount(MemberDto memberDto) {
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
    }

    private void validateSignupRequest(MemberDto memberDto) {
        //MemberInfoValidator memberInfoValidator = new MemberInfoValidator();

        // 이메일 중복 검사
        if (memberRepository.findByEmail(memberDto.getEmail()).isPresent()) {
            throw new MemberException(MemberResponseMessage.DUPLICATED_EMAIL);
        }

        // 아이디 중복 검사
        if (memberRepository.findByLoginId(memberDto.getLoginId()).isPresent()) {
            throw new MemberException(MemberResponseMessage.DUPLICATED_LOGIN_ID);
        }
    }

    @Transactional
    public void verifySignup(Long memberId, String verificationCode) {
        // 메일 서비스를 사용한 검증
        // 코드 인증 안되면 exception 발생
        mailService.verifySignup(memberId, verificationCode);

        MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MemberResponseMessage.NOT_FOUND)
        );

        memberEntity.setRole(MemberRole.BASIC);
    }

    public MemberResponseDto getMemberInfo(UserDetails userDetails, Long memberId) {
        MemberEntity memberEntity = (MemberEntity) userDetails;

        if (memberEntity.getMemberId() != memberId) {
            throw new AuthorizationException(AuthMessage.INVALID_PRINCIPLE);
        }

        MemberEntity memberInfo = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MemberResponseMessage.NOT_FOUND)
        );

        MemberDto memberDto = memberInfo.toDto();
        decryptMemberDto(memberDto);
        memberDto.setPassword("");

        // 주소 목록 추가
        List<AddrDto> addrDtoList = addrService.getAddrList(memberInfo, memberId);

        return new MemberResponseDto(memberDto, addrDtoList);
    }

    @Transactional
    public void updatePassword(UserDetails userDetails, MemberRequestVo passwordRequestDto, Long memberId) {
        if (passwordRequestDto.getPassword() == null || passwordRequestDto.getConfirmPassword() == null) {
            throw new MemberException(MemberResponseMessage.MISSING_REQUIRED_FIELD);
        }

        MemberEntity memberEntity = (MemberEntity) userDetails;

        if (memberEntity.getMemberId() - memberId != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED);
        }

        String password = passwordRequestDto.getPassword();
        String confirmPassword = passwordRequestDto.getConfirmPassword();

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, memberEntity.getPassword())) {
            throw new MemberException(MemberResponseMessage.INVALID_PASSWORD);
        }

        // 새 비밀번호에 대한 패턴 검증
        if (!new MemberInfoValidator().checkPattern(PatternConfig.passwordPattern, confirmPassword)) {
            throw new MemberException(MemberResponseMessage.UNMATCHED_PASSWORD);
        }

        // 영속 상태 엔티티 가져오기
        MemberEntity managedEntity = memberRepository.findById(memberEntity.getMemberId()).orElseThrow(
                () -> new MemberException(MemberResponseMessage.NOT_FOUND)
        );

        managedEntity.setPassword(passwordEncoder.encode(confirmPassword));
    }


    @Transactional
    public void updatePhoneNumber(UserDetails userDetails, MemberRequestVo phoneNumberUpdateRequestDto, Long memberId) {
        if (phoneNumberUpdateRequestDto.getPhoneNumber() == null) {
            throw new MemberException(MemberResponseMessage.MISSING_REQUIRED_FIELD);
        }

        MemberEntity memberEntity = (MemberEntity) userDetails;

        if (memberEntity.getMemberId() - memberId != 0) {
            throw new AuthorizationException(AuthMessage.AUTHORIZATION_DENIED);
        }

        MemberEntity memberInfo = memberRepository.findById(memberEntity.getMemberId()).orElseThrow(
                ()->new MemberException(MemberResponseMessage.NOT_FOUND)
        );

        memberInfo.setPhoneNumber(userInformationEncoder.encrypt(phoneNumberUpdateRequestDto.getPhoneNumber(), saltGenerator.generateSalt()));
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
