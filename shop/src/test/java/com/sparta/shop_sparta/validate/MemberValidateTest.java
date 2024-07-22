package com.sparta.shop_sparta.validate;

import com.sparta.shop_sparta.domain.dto.member.MemberDto;
import com.sparta.shop_sparta.validator.member.EntityFieldValidator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;


public class MemberValidateTest {

    @Test
    @DisplayName("필수 파라미터 포함 여부 성공 테스트")
    void parameterCheckSuccessTest(){
        MemberDto memberDto = MemberDto.builder()
                .memberName("지웅이").phoneNumber("010-2720-9158").addr("경기도 안산시 단원구 고잔2길 9").email("sin9158@naver.com")
                .loginId("sin9158").addrDetail("541호").password("testPassword1@")
                .build();

        EntityFieldValidator entityFieldValidator = new EntityFieldValidator();
        Boolean result = entityFieldValidator.validateParams(memberDto.toEntity());

        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("필수 파라미터 포함 여부 실패 테스트")
    void parameterCheckFailTest(){
        MemberDto memberDto = MemberDto.builder()
                .memberName("지웅이").phoneNumber("010-2720-9158").addr("경기도 안산시 단원구 고잔2길 9")
                .loginId("sin9158").addrDetail("541호").password("testPassword1@")
                .build();

        EntityFieldValidator entityFieldValidator = new EntityFieldValidator();
        Boolean result = entityFieldValidator.validateParams(memberDto.toEntity());

        assertThat(result).isEqualTo(false);
    }

    @ParameterizedTest
    @DisplayName("패스워드 패턴 매칭[정규식] 성공 테스트")
    @ValueSource(strings = {"sSdsd!@1212!@", "zxcasdvDd23!@#", "tDest12123!@", "123adsDa@4", "AD!@#na123me",
            "adAd3r!@!@A", "Abac#21a2#"})
    void passwordPatternMatchSuccessTest(String password) {
        String regex = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=])(?=.*[0-9]).{10,16}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        assertThat(matcher.matches()).isEqualTo(true);
    }

    @ParameterizedTest
    @DisplayName("패스워드 패턴 매칭[정규식] 실패 테스트")
    @ValueSource(strings = {"adD123", "!@#!@#112323", "!@# $% @# !#", "", "나는지웅이다누구도막을수없다", "asd123asd", "aDaD!@#$#","            "})
    void passwordPatternMatchFailTest(String password) {
        String regex = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=])(?=.*[0-9]).{10,16}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        assertThat(matcher.matches()).isEqualTo(false);
    }

    @ParameterizedTest
    @DisplayName("아이디 형식[정규식] 성공 테스트")
    @ValueSource(strings = {"sin9158", "hypeboy", "hypeboy12", "HypeBoy"})
    void loginIdPatternMatchSuccessTest(String loginId) {
        String regex = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{5,25}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(loginId);

        assertThat(matcher.matches()).isEqualTo(true);
    }

    @ParameterizedTest
    @DisplayName("아이디 형식[정규식] 성공 테스트")
    @ValueSource(strings = {"", "1234123", "hypeboy12Fdiekkdddkkksassadfddfdiferqewq", "a123", "HypeBoy!@", "나는지웅이다"})
    void loginIdPatternMatchFailTest(String loginId) {
        String regex = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{5,25}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(loginId);

        assertThat(matcher.matches()).isEqualTo(false);
    }
}
