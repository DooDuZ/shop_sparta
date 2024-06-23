package com.sparta.shop_sparta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MailConfig {
    @Value("${REQUEST_URL}")
    public static String requestUrl;
    public static final String MAIL_TITLE = "두두지월드 가입 인증 메일";
    @Value("${MAIL_FROM}")
    public static String from;
    public static final String DOMAIN_NAME = "두두지월드";

    @Bean
    JavaMailSender javaMailSender(){
        return new JavaMailSenderImpl();
    }
}
