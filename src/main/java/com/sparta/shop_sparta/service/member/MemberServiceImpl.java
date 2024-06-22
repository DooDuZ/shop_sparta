package com.sparta.shop_sparta.service.member;

import com.sparta.shop_sparta.domain.dto.member.LoginResponseDto;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.repository.MemberRepository;
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

        MemberEntity memberEntity = memberRepository.save(memberDto.toEntity());
        MemberDto responseDto = memberEntity.toDto();
        responseDto.setPassword("");
        return responseDto;
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
