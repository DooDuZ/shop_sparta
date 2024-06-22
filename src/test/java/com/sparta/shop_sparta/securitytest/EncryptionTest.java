package com.sparta.shop_sparta.securitytest;

import com.sparta.shop_sparta.config.security.encoder.SaltGenerator;
import com.sparta.shop_sparta.config.security.encoder.UserInformationEncoder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EncryptionTest {

    @Autowired
    SaltGenerator saltGenerator;

    @Autowired
    UserInformationEncoder userInformationEncoder;

    @Test
    @DisplayName("암호화 후 복호화 값이 동일한지 확인합니다.")
    void encodeTest() {
        String salt = saltGenerator.generateSalt();

        String password = "1234";

        String encodedPassword = userInformationEncoder.encrypt(password, salt);
        String decodedPassword = userInformationEncoder.decrypt(encodedPassword, salt);

        Assertions.assertThat(decodedPassword).isEqualTo(password);
    }
}
