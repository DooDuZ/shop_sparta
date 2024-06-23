package com.sparta.shop_sparta.service;

import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.constant.member.MemberRole;
import com.sparta.shop_sparta.repository.AddrRepository;
import com.sparta.shop_sparta.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberServiceImpl {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private AddrRepository addrRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("test")
    void createAccountTest(){
        MemberDto memberDto = MemberDto.builder().memberName("지웅이").email("sin9158@naver.com").loginId("sin9158")
                .password("1234Password!").addr("경기도 안산시 단원구 고잔2길 9").addrDetail("541호")
                .phoneNumber("01027209158").role(MemberRole.BASIC).build();

        String password = "1234Password!";

        memberDto.setPassword(bCryptPasswordEncoder.encode(memberDto.getPassword()));

        System.out.println(memberDto.getAddr());

        // Mock the behavior of memberRepository.save
        MemberEntity mockMemberEntity = memberDto.toEntity();
        mockMemberEntity.setMemberId(1L); // 임의의 ID 설정
        Mockito.when(memberRepository.save(Mockito.any(MemberEntity.class))).thenReturn(mockMemberEntity);

        MemberEntity memberEntity = memberRepository.save(memberDto.toEntity());

        System.out.println(memberEntity);

        Assertions.assertThat(memberEntity.getMemberId()).isNotNull();
        Assertions.assertThat(bCryptPasswordEncoder.matches(password, memberEntity.getPassword())).isEqualTo(true);

        // AddrDto addrDto = AddrDto.builder().addr(memberDto.getAddr()).addrDetail(memberDto.getAddrDetail()).build();
        // AddrEntity addrEntity = addrRepository.save(addrDto.toEntity());


        // Assertions.assertThat(addrEntity.getAddrId()).isNotNull();
    }

}
