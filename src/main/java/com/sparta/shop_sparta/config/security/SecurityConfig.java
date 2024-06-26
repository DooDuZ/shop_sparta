package com.sparta.shop_sparta.config.security;

import com.sparta.shop_sparta.config.security.jwt.JwtAuthenticationFilter;
import com.sparta.shop_sparta.config.security.jwt.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LoginSuccessHandler loginSuccessHandler;

    // 개발 초기 단계 모든 접근 허용
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.
                authorizeHttpRequests( authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login","/member/", "/member/verification","/auth/token" ).permitAll()
                                .anyRequest().hasAnyRole("BASIC", "ADMIN")
                )
                .sessionManagement( sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // UsernamePasswordAuthenticationFilter의 인증이 끝난 뒤 successHandler 실행
                .formLogin(formLogin ->
                        formLogin
                                .successHandler(loginSuccessHandler)
                                .permitAll()
                )

                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
