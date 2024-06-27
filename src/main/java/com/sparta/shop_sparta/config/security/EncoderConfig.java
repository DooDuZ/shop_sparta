package com.sparta.shop_sparta.config.security;

import com.sparta.shop_sparta.util.encoder.TokenUsernameEncoder;
import com.sparta.shop_sparta.util.encoder.SaltGenerator;
import com.sparta.shop_sparta.util.encoder.UserInformationEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class EncoderConfig {
    @Value("${SECRET_KEY}")
    public String secretKey;

    @Bean
    public SaltGenerator saltGenerator() {
        return new SaltGenerator();
    }

    // 복호화 가능 암호화 인코더
    @Bean
    public UserInformationEncoder userInformationEncoder(){
        return new UserInformationEncoder(secretKey);
    }

    // 패스워드 인코더
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenUsernameEncoder tokenUsernameEncoder(){
        return new TokenUsernameEncoder();
    }
}
