package com.sparta.shop_sparta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberRole;
import com.sparta.shop_sparta.service.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper; // ObjectMapper 주입

    @Test
    @DisplayName("회원가입 메서드 요청 확인 Test")
    void requestCheckingTest() throws Exception {
        MemberEntity memberEntity = MemberEntity.builder().memberId(1L).memberName("지웅이").loginId("sin9158")
                .password("1234").role(MemberRole.ADMIN).email("sin9158@naver.com").phoneNumber("01027209158").build();

        mockMvc.perform(MockMvcRequestBuilders.post("/member/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberEntity)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("memberId").exists()) // ID가 생성되었는지 검증
                .andExpect(MockMvcResultMatchers.jsonPath("memberName").value("지웅이")); // 이름이 예상대로 들어갔는지 검증
    }
}
