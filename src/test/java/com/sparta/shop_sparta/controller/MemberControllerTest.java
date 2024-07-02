package com.sparta.shop_sparta.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

import com.sparta.shop_sparta.controller.member.MemberControllerImpl;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.service.member.MemberServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;


@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {
    @InjectMocks
    private MemberControllerImpl memberController;

    @Mock
    private BindingResult bindingResult;

    @Test
    @DisplayName("회원가입 메서드 요청 확인 Test")
    void requestCheckingTest() throws Exception {
        // given
        MemberDto memberDto = MemberDto.builder().memberName("지웅이").email("sin9158@naver.com")
                .addr("경기도 안산시 단원구 고잔2길 9").addrDetail("541호")
                .phoneNumber("01027209158").build();

        given(bindingResult.hasErrors()).willReturn(true);

        // when
        ResponseEntity<?> response = memberController.createAccount(memberDto, bindingResult);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }
}
