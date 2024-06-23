package com.sparta.shop_sparta.validate;

import com.sparta.shop_sparta.validator.member.PasswordValidator;
import com.sparta.shop_sparta.validator.member.PasswordValidatorImpl;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class MemberValidateTest {
    @ParameterizedTest
    @DisplayName("패스워드 패턴 매칭[정규식] 성공 테스트")
    @ValueSource(strings = {"sSdsd!@1212!@", "zxcasdvDd23!@#", "tDest12123!@", "123adsDa@4", "AD!@#na123me",
            "adAd3r!@!@A", "Abac#21a2#"})
    void passwordPatternMatchSuccessTest(String password) {
        PasswordValidator passwordValidator = new PasswordValidatorImpl();

        if (!passwordValidator.checkPasswordPattern(password)) {
            System.out.println(password);
        }

        Assertions.assertThat(passwordValidator.checkPasswordPattern(password)).isEqualTo(true);
    }

    @ParameterizedTest
    @DisplayName("패스워드 패턴 매칭[정규식] 실패 테스트")
    @ValueSource(strings = {"adD123", "!@#!@#112323", "!@# $% @# !#", "", "나는지웅이다누구도막을수없다", "asd123asd", "aDaD!@#$#","            "})
    void passwordPatternMatchFailTest(String password) {
        PasswordValidator passwordValidator = new PasswordValidatorImpl();

        // System.out.println(password);
        if (!passwordValidator.checkPasswordPattern(password)) {
            System.out.println(password);
        }

        Assertions.assertThat(passwordValidator.checkPasswordPattern(password)).isEqualTo(false);
    }

    @ParameterizedTest
    @DisplayName("아이디 형식[정규식] 성공 테스트")
    @ValueSource(strings = {"sin9158", "hypeboy", "hypeboy12", "HypeBoy"})
    void loginIdPatternMatchSuccessTest(String loginId) {
        String regex = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{5,25}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(loginId);

        Assertions.assertThat(matcher.matches()).isEqualTo(true);
    }

    @ParameterizedTest
    @DisplayName("아이디 형식[정규식] 성공 테스트")
    @ValueSource(strings = {"", "1234123", "hypeboy12Fdiekkdddkkksassadfddfdiferqewq", "a123", "HypeBoy!@", "나는지웅이다"})
    void loginIdPatternMatchFailTest(String loginId) {
        String regex = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{5,25}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(loginId);

        Assertions.assertThat(matcher.matches()).isEqualTo(false);
    }
}
