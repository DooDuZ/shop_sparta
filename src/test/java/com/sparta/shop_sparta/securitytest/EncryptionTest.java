package com.sparta.shop_sparta.securitytest;

import com.sparta.shop_sparta.config.security.encoder.SaltGenerator;
import com.sparta.shop_sparta.config.security.encoder.UserInformationEncoder;
import com.sparta.shop_sparta.validator.password.PasswordValidator;
import com.sparta.shop_sparta.validator.password.PasswordValidatorImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class EncryptionTest {

    @Autowired
    SaltGenerator saltGenerator;

    @Autowired
    UserInformationEncoder userInformationEncoder;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @ParameterizedTest
    @DisplayName("데이터 암호화 후 복호화 값이 동일한지 확인합니다.")
    @ValueSource(strings = {"!@#!$ASd", "zxcvd23!@#", "test12!@", "1234", "name", "addr", "경기도 안산시 단원구 고잔2길 9 하이베라스 541호"})
    void encodeTest(String password) {
        String salt = saltGenerator.generateSalt();

        String encodedPassword = userInformationEncoder.encrypt(password, salt);
        String decodedPassword = userInformationEncoder.decrypt(encodedPassword, salt);

        Assertions.assertThat(decodedPassword).isEqualTo(password);
    }

    @ParameterizedTest
    @DisplayName("패스워드 패턴 매칭[정규식] 성공 테스트")
    @ValueSource(strings = {"sSdsd!@1212!@", "zxcasdvDd23!@#", "tDest12123!@", "123adsDa@4", "AD!@#na123me", "adAd3r!@!@A", "Abac#21a2#"})
    void passwordPatternMatchTest(String password){
        PasswordValidator passwordValidator = new PasswordValidatorImpl();

        // System.out.println(password);
        if (!passwordValidator.checkPattern(password)){
            System.out.println(password);
        }

        Assertions.assertThat(passwordValidator.checkPattern(password)).isEqualTo(true);
    }


    @Test
    @DisplayName("패스워드 일치 테스트")
    void passwordEncodeTest(){
        String password = "tDest12123!@";
        String DBPassword = bCryptPasswordEncoder.encode(password);

        Assertions.assertThat(bCryptPasswordEncoder.matches(password, DBPassword)).isEqualTo(true);
    }
}