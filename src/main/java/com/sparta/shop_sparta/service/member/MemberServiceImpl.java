package com.sparta.shop_sparta.service.member;

import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import com.sparta.shop_sparta.domain.dto.member.LoginResponseDto;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.constant.member.MemberRole;
import com.sparta.shop_sparta.exception.CreateAccountException;
import com.sparta.shop_sparta.repository.MemberRepository;
import com.sparta.shop_sparta.validator.member.EntityFieldValidator;
import com.sparta.shop_sparta.validator.member.pattern.MemberInfoValidator;
import com.sparta.shop_sparta.validator.member.pattern.PatternConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService, UserDetailsService {
    MemberRepository memberRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    MemberServiceImpl(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public MemberDto createAccount(MemberDto memberDto) {
        memberDto.setPassword(bCryptPasswordEncoder.encode(memberDto.getPassword()));

        // 가입 정보 유효성 검사
        validateSignupRequest(memberDto);

        MemberEntity memberEntity = memberRepository.save(memberDto.toEntity());
        memberEntity.setRole(MemberRole.BASIC);

        return memberEntity.toDto();
    }

    private void validateSignupRequest(MemberDto memberDto){
        // 필수 파라미터 검사
        if (new EntityFieldValidator().validateParams(memberDto.toEntity())){
            throw new CreateAccountException(MemberResponseMessage.MISSING_REQUIRED_FIELD.getMessage());
        }

        // 아이디 정규식 검사
        if(new MemberInfoValidator(PatternConfig.loginIdPattern).checkPattern(memberDto.getLoginId())){
            throw new CreateAccountException(MemberResponseMessage.UNMATCHED_ID.getMessage());
        }
        // 이메일 정규식 검사
        if(new MemberInfoValidator(PatternConfig.emailPattern).checkPattern(memberDto.getLoginId())){
            throw new CreateAccountException(MemberResponseMessage.UNMATCHED_EMAIL.getMessage());
        }
        // 패스워드 정규식 검사
        if(new MemberInfoValidator(PatternConfig.passwordPattern).checkPattern(memberDto.getLoginId())){
            throw new CreateAccountException(MemberResponseMessage.UNMATCHED_PASSWORD.getMessage());
        }
        // 휴대폰 번호 정규식 검사
        if(new MemberInfoValidator(PatternConfig.phoneNumberPattern).checkPattern(memberDto.getLoginId())){
            throw new CreateAccountException(MemberResponseMessage.UNMATCHED_PHONENUMBER.getMessage());
        }

        // 아이디 중복 검사
        if(memberRepository.findByLoginId(memberDto.getLoginId()).isPresent()){
            throw new CreateAccountException(MemberResponseMessage.DUPLICATED_LOGIN_ID.getMessage());
        }
        // 이메일 중복 검사
        if(memberRepository.findByEmail(memberDto.getEmail()).isPresent()){
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
