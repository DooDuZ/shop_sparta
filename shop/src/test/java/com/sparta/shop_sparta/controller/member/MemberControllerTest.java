package com.sparta.shop_sparta.controller.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.service.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {
    private MockMvc mockMvc;   // HTTP 호출을 위한 MockMVC 사용

    @InjectMocks
    MemberController memberController;

    @Mock
    MemberService memberService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @Nested
    @DisplayName("[회원가입 요청 유효성 검사] ")
    class CreateAccountTest{
        @Test
        @DisplayName("회원 가입 성공 테스트")
        void createAccountSuccessTest() throws Exception {
            // Given
            MemberDto memberDto = MemberDto.builder().memberName("지웅이").password("testPassword1@").addr("어딘가").addrDetail("어어딘가")
                    .email("sin9158@naver.com").loginId("sin9158").phoneNumber("010-2720-9158").build();

            // When
            ResultActions resultActions = mockMvc.perform(post("/member")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(memberDto)));

            // then
            resultActions.andExpect(status().isOk());
        }

        @ParameterizedTest
        @ValueSource(strings = {"testPassword!2", "!@# $%as1234", "1234ababAbc#$", "MAMAmamaBoi43*", "April123!@"})
        @DisplayName("비밀번호 유효성 성공 테스트")
        void passwordPatternSuccessTest(String password) throws Exception {
            // Given
            MemberDto memberDto = MemberDto.builder().memberName("지웅이").password(password).addr("어딘가").addrDetail("어어딘가")
                    .email("sin9158@naver.com").phoneNumber("010-2720-9158").loginId("sin9158").build();

            // When
            ResultActions resultActions = mockMvc.perform(post("/member")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(memberDto)));

            // then
            resultActions.andExpect(status().isOk());
        }


        @ParameterizedTest
        @ValueSource(strings = {"", "  ", "1234", "한글비밀번호", "한글과1Aa#ㅁ", "!@#!@#$%$$#%!", "short12!", "loOooooooooooooooooooooooooooooong12!"})
        @DisplayName("비밀번호 유효성 실패 테스트")
        void passwordPatternFailTest(String password) throws Exception {
            // Given
            MemberDto memberDto = MemberDto.builder().memberName("지웅이").password(password).addr("어딘가").addrDetail("어어딘가")
                    .email("sin9158@naver.com").phoneNumber("010-2720-9158").loginId("sin9158").build();

            // When
            ResultActions resultActions = mockMvc.perform(post("/member")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(memberDto)));

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"010-2720-9158", "010-9999-1111", "010-3333-4445", "011-389-0404", "016-3939-2939"})
        @DisplayName("휴대폰 번호 유효성 성공 테스트")
        void phoneNumberPatternSuccessTest(String phoneNumber) throws Exception {
            // Given
            MemberDto memberDto = MemberDto.builder().memberName("지웅이").password("testPassword1@").addr("어딘가").addrDetail("어어딘가")
                    .email("sin9158@naver.com").phoneNumber(phoneNumber).loginId("sin9158").build();

            // When
            ResultActions resultActions = mockMvc.perform(post("/member")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(memberDto)));

            // then
            resultActions.andExpect(status().isOk());
        }

        @ParameterizedTest
        @ValueSource(strings = {"001-2720-9158", "31-99-1111", "5", "032-389-0404", "--나숫자아니다--", "01028291919", "013425"})
        @DisplayName("휴대폰 번호 유효성 실패 테스트")
        void phoneNumberPatternFailTest(String phoneNumber) throws Exception {
            // Given
            MemberDto memberDto = MemberDto.builder().memberName("지웅이").password("testPassword1@").addr("어딘가").addrDetail("어어딘가")
                    .email("sin9158@naver.com").phoneNumber(phoneNumber).loginId("sin9158").build();

            // When
            ResultActions resultActions = mockMvc.perform(post("/member")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(memberDto)));

            // then
            resultActions.andExpect(status().isBadRequest());
        }


        @ParameterizedTest
        @ValueSource(strings = {"지웅이", "wldnddl", "오대수"})
        @DisplayName("이름 입력 성공 테스트")
        void memberNamePatternSuccessTest(String memberName) throws Exception {
            // Given
            MemberDto memberDto = MemberDto.builder().memberName(memberName).password("testPassword1@").addr("어딘가").addrDetail("어어딘가")
                    .email("sin9158@naver.com").phoneNumber("010-2720-9158").loginId("sin9158").build();

            // When
            ResultActions resultActions = mockMvc.perform(post("/member")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(memberDto)));

            // then
            resultActions.andExpect(status().isOk());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "        "})
        @DisplayName("이름 입력 실패 테스트")
        void memberNamePatternFailTest(String memberName) throws Exception {
            // Given
            MemberDto memberDto = MemberDto.builder().memberName(memberName).password("testPassword1@").addr("어딘가").addrDetail("어어딘가")
                    .email("sin9158@naver.com").phoneNumber("010-2720-9158").loginId("sin9158").build();

            // When
            ResultActions resultActions = mockMvc.perform(post("/member")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(memberDto)));

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"sin9158@naver.com", "shin91588@gmail.com", "abbppd@hanmail.net", "test@alpha.org"})
        @DisplayName("이메일 입력 성공 테스트")
        void emailPatternSuccessTest(String email) throws Exception {
            // Given
            MemberDto memberDto = MemberDto.builder().memberName("지웅이").password("testPassword1@").addr("어딘가").addrDetail("어어딘가")
                    .email(email).phoneNumber("010-2720-9158").loginId("sin9158").build();

            // When
            ResultActions resultActions = mockMvc.perform(post("/member")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(memberDto)));

            // then
            resultActions.andExpect(status().isOk());
        }

        @ParameterizedTest
        @ValueSource(strings = {"@naver.com", "abbppd@net","shin91588@gmail",  "aab@oksite.co..kr", "test@alphaorg"})
        // "abbppd@net", "shin91588@gmail"  이놈 ok 나옴
        @DisplayName("이메일 입력 실패 테스트")
        void emailPatternFailTest(String email) throws Exception {
            // Given
            MemberDto memberDto = MemberDto.builder().memberName("지웅이").password("testPassword1@").addr("어딘가").addrDetail("어어딘가")
                    .email(email).phoneNumber("010-2720-9158").loginId("sin9158").build();

            // When
            ResultActions resultActions = mockMvc.perform(post("/member")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(memberDto)));

            // then
            resultActions.andExpect(status().isBadRequest());
        }
    }
}
