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
    private final UserInformationEncoder userInformationEncoder;

    // 복호화 가능한 인코더
    // private final UserInformationEncoder userInformationEncoder;


    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        MemberEntity memberEntity = memberRepository.findByLoginId(loginId).orElseThrow(
                () -> new UsernameNotFoundException(MemberResponseMessage.NOT_FOUND.getMessage())
        );


        Set<GrantedAuthority> authorities =new HashSet<>();
        // 권한 추가
        authorities.add(new SimpleGrantedAuthority(memberEntity.getRole().getGrade()));

        //MemberDto memberDto = memberEntity.toDto();

        decryptMemberEntity(memberEntity);
        memberEntity.setAuthorities(authorities);

        return memberEntity;
    }

    private void decryptMemberEntity(MemberEntity memberEntity) {
        memberEntity.setEmail(userInformationEncoder.decrypt(memberEntity.getEmail()));
        memberEntity.setPhoneNumber(userInformationEncoder.decrypt(memberEntity.getPhoneNumber()));
        memberEntity.setMemberName(userInformationEncoder.decrypt(memberEntity.getMemberName()));
    }
}
