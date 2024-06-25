package com.sparta.shop_sparta.service.member;

import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.repository.MemberRepository;
import com.sparta.shop_sparta.util.encoder.UserInformationEncoder;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 복호화 가능한 인코더
    private final UserInformationEncoder userInformationEncoder;


    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        MemberEntity memberEntity = memberRepository.findByLoginId(loginId).orElseThrow(
                () -> new UsernameNotFoundException(MemberResponseMessage.NOT_FOUND.getMessage())
        );

        Set<GrantedAuthority> authorities =new HashSet<>();
        // 권한 추가
        authorities.add(new SimpleGrantedAuthority(memberEntity.getRole().getGrade()));

        MemberDto memberDto = memberEntity.toDto();

        decryptMemberDto(memberDto);
        memberDto.setAuthorities(authorities);

        return memberDto;
    }

    private void decryptMemberDto(MemberDto memberDto) {
        memberDto.setEmail(userInformationEncoder.decrypt(memberDto.getEmail()));
        memberDto.setPhoneNumber(userInformationEncoder.decrypt(memberDto.getPhoneNumber()));
        memberDto.setMemberName(userInformationEncoder.decrypt(memberDto.getMemberName()));
    }
}
