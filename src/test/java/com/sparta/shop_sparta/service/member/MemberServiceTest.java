package com.sparta.shop_sparta.service.member;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sparta.shop_sparta.constant.member.AuthMessage;
import com.sparta.shop_sparta.constant.member.MemberResponseMessage;
import com.sparta.shop_sparta.domain.dto.member.AddrDto;
import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.domain.dto.member.MemberRequestVo;
import com.sparta.shop_sparta.domain.dto.member.MemberResponseDto;
import com.sparta.shop_sparta.domain.entity.member.AddrEntity;
import com.sparta.shop_sparta.domain.entity.member.MemberEntity;
import com.sparta.shop_sparta.exception.AuthorizationException;
import com.sparta.shop_sparta.exception.MemberException;
import com.sparta.shop_sparta.repository.AddrRepository;
import com.sparta.shop_sparta.repository.MemberRepository;
import com.sparta.shop_sparta.service.member.addr.AddrService;
import com.sparta.shop_sparta.service.member.verify.MailService;
import com.sparta.shop_sparta.util.encoder.SaltGenerator;
import com.sparta.shop_sparta.util.encoder.UserInformationEncoder;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AddrRepository addrRepository;

    @Mock
    private MailService mailService;

    @Mock
    private AddrService addrService;

    // Encoder -> bean 대신 new 로 사용 가능
    @Mock
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private SaltGenerator saltGenerator = new SaltGenerator();

    @Mock
    private UserInformationEncoder userInformationEncoder = new UserInformationEncoder("mySecret");


    MemberDto existingMember;
    MemberDto requestDto;

    @BeforeEach
    void init(){
        existingMember = MemberDto.builder().memberName("지웅이").email("sin9158@naver.com").loginId("sin9158")
                .password("testPassword1@").addr("경기도 안산시 단원구 고잔2길 9").addrDetail("541호")
                .phoneNumber("010-2720-9158").build();
        requestDto = MemberDto.builder().memberName("누렁이").email("shin9158@naver.com").loginId("shin9158")
                .password("testPassword1@").addr("경기도 안산시 단원구 고잔2길 9").addrDetail("541호")
                .phoneNumber("010-2720-9158").build();
    }

    @Nested
    @DisplayName("[회원가입 테스트]")
    class CreateMemberTest{
        @Test
        @DisplayName("성공 테스트")
        void createSuccessTest(){
            // given
            when(memberRepository.save(Mockito.any(MemberEntity.class))).thenReturn(requestDto.toEntity());

            // when
            memberService.createAccount(requestDto);

            // then
            verify(mailService).sendVerification(any(MemberDto.class));
            verify(addrService).addAddr(any(MemberEntity.class), any(AddrDto.class));
        }

        @Test
        @DisplayName("이미 존재하는 이메일이 입력되면 실패합니다.")
        void duplicatedEmailTest(){
            // given
            requestDto.setEmail("sin9158@naver.com");

            when(memberRepository.findByEmail(existingMember.getEmail())).thenReturn(Optional.of(new MemberEntity()));

            // when & then
            assertThatThrownBy( ()->{
                memberService.createAccount(requestDto);
            }).isInstanceOf(MemberException.class).hasMessage(MemberResponseMessage.DUPLICATED_EMAIL.getMessage());
        }

        @Test
        @DisplayName("존재하는 아이디가 입력되면 실패합니다.")
        void duplicatedLoginIdTest(){
            // given
            requestDto.setLoginId("sin9158");

            when(memberRepository.findByLoginId(existingMember.getLoginId())).thenReturn(Optional.of(new MemberEntity()));

            // when & then
            assertThatThrownBy( ()->{
                memberService.createAccount(requestDto);
            }).isInstanceOf(MemberException.class).hasMessage(MemberResponseMessage.DUPLICATED_LOGIN_ID.getMessage());
        }
    }

    @Nested
    @DisplayName("[회원 정보 수정 테스트]")
    class UpdateMemberTest{
        @Mock
        private UserDetails userDetails;

        @Mock
        private MemberEntity memberEntity;

        @Test
        @DisplayName("전화번호 업데이트 성공")
        void updatePhoneNumberSuccessTest() {
            // given
            MemberRequestVo memberRequestVo = MemberRequestVo.builder().phoneNumber("010-2720-9158").build();
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));

            // when then
            Assertions.assertDoesNotThrow(
                    () -> memberService.updatePhoneNumber(memberEntity, memberRequestVo)
            );
        }

        @Test
        @DisplayName("전화번호 값이 null이면 업데이트에 실패합니다.")
        void updatePhoneNumberFailTest() {
            // given
            MemberRequestVo memberRequestVo = MemberRequestVo.builder().phoneNumber(null).build();

            // when then
            assertThatThrownBy(
                    ()->memberService.updatePhoneNumber(memberEntity, memberRequestVo)
            ).isInstanceOf(MemberException.class).hasMessage(MemberResponseMessage.MISSING_REQUIRED_FIELD.getMessage());
        }
    }

    @Nested
    @DisplayName("[메일 인증 테스트]")
    class verifyTest{
        @Test
        @DisplayName("인증 성공")
        void verifySignupSuccessTest(){
            // given
            Long memberId = 1L;
            String verificationCode = "393904";

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(new MemberEntity()));

            // when
            memberService.verifySignup(memberId, verificationCode);

            // then
            verify(mailService).verifySignup(memberId, verificationCode);
        }

        @Test
        @DisplayName("회원 정보를 찾지 못하면 실패합니다.")
        void verifySignupMemberIdFailTest(){
            // given
            Long memberId = 1L;
            String verificationCode = "393904";

            when(memberRepository.findById(memberId)).thenReturn(Optional.ofNullable(null));

            // when then
            assertThatThrownBy( ()->{
                memberService.verifySignup(memberId, verificationCode);
            }).isInstanceOf(MemberException.class).hasMessage(MemberResponseMessage.NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("인증코드가 일치하지 않으면 실패합니다.")
        void verifySignupVerifyCodeFailTest(){
            // given
            Long memberId = 1L;
            String verificationCode = "393904";

            doThrow(new MemberException(MemberResponseMessage.UNMATCHED_VERIFICATION_CODE.getMessage()))
                    .when(mailService).verifySignup(memberId, verificationCode);

            // when then
            assertThatThrownBy( ()->{
                memberService.verifySignup(memberId, verificationCode);
            }).isInstanceOf(MemberException.class).hasMessage(MemberResponseMessage.UNMATCHED_VERIFICATION_CODE.getMessage());
        }
    }

    @Test
    @DisplayName("회원 정보 조회 성공 테스트")
    void getMemberInfoSuccessTest(){
        // given
        Long memberId = 1L;
        MemberEntity memberEntity = existingMember.toEntity();
        memberEntity.setMemberId(1L);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));

        // when
        MemberResponseDto responseDto = memberService.getMemberInfo(memberEntity, memberId);

        // then
        assertThat(responseDto.getLoginId()).isEqualTo(existingMember.getLoginId());

    }

    @Test
    @DisplayName("회원 정보 조회 실패 테스트")
    void getMemberInfoFailTest(){
        // given
        Long memberId = 2L;
        MemberEntity memberEntity = existingMember.toEntity();
        memberEntity.setMemberId(1L);

        // when then
        assertThatThrownBy( ()->{
            memberService.getMemberInfo(memberEntity, memberId);
        }).isInstanceOf(AuthorizationException.class).hasMessage(AuthMessage.INVALID_PRINCIPLE.getMessage());
    }
}
