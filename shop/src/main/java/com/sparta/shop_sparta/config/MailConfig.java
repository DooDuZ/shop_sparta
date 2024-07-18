package com.sparta.shop_sparta.config;

import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:/application.yml")
@ConfigurationProperties(prefix = "spring.mail")
@Getter
@Setter
public class MailConfig {
    @Value("${REQUEST_URL}")
    public String requestUrl;
    public String mailTitle = "두두지월드 가입 인증 메일";
    @Value("${MAIL_FROM}")
    public String from;
    public String domainName = "두두지월드";

    private String username;
    private String password;
    private int port;
    private Properties properties;

    private final Integer timeToLive = 60 * 24 * 7;

    @Bean
    JavaMailSender javaMailSender(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.naver.com");
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);

        javaMailSender.setPort(port);
        javaMailSender.setJavaMailProperties(properties);

        return javaMailSender;
    }
}
