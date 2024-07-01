package com.sparta.shop_sparta.service;

import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.constant.member.MemberRole;
import com.sparta.shop_sparta.repository.AddrRepository;
import com.sparta.shop_sparta.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@ExtendWith(MockitoExtension.class)
public class MemberServiceImplTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AddrRepository addrRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("test")
    void createAccountTest(){
        // given
        String password = "1234Password!";
        MemberDto memberDto = MemberDto.builder().memberName("지웅이").email("sin9158@naver.com").loginId("sin9158")
                .password(password).addr("경기도 안산시 단원구 고잔2길 9").addrDetail("541호")
                .phoneNumber("01027209158").role(MemberRole.BASIC).build();
        memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));

        MemberEntity mockMemberEntity = memberDto.toEntity();
        mockMemberEntity.setMemberId(1L); // 임의의 ID 설정

        // when
        Mockito.when(memberRepository.save(Mockito.any(MemberEntity.class))).thenReturn(mockMemberEntity);
        MemberEntity memberEntity = memberRepository.save(memberDto.toEntity());

        // then
        Assertions.assertThat(memberEntity.getMemberId()).isNotNull();
        Assertions.assertThat(passwordEncoder.matches(password, memberEntity.getPassword())).isEqualTo(true);

        // AddrDto addrDto = AddrDto.builder().addr(memberDto.getAddr()).addrDetail(memberDto.getAddrDetail()).build();
        // AddrEntity addrEntity = addrRepository.save(addrDto.toEntity());


        // Assertions.assertThat(addrEntity.getAddrId()).isNotNull();
    }

}
