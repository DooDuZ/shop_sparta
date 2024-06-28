package com.sparta.shop_sparta.config.security;

import com.sparta.shop_sparta.config.security.jwt.JwtAuthenticationFilter;
import com.sparta.shop_sparta.config.security.jwt.LoginAuthenticationFilter;
import com.sparta.shop_sparta.config.security.jwt.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LoginSuccessHandler loginSuccessHandler;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.
                authorizeHttpRequests( authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/index.html","/","/login", "/member/verification","/auth/token" ).permitAll()
                                .requestMatchers(HttpMethod.POST, "/member").permitAll()
                                .anyRequest().hasAnyRole("BASIC", "SELLER","ADMIN")
                )
                .sessionManagement( sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(loginAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin( formLogin -> formLogin.disable())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public LoginAuthenticationFilter loginAuthenticationFilter(AuthenticationManager authenticationManager) {
        LoginAuthenticationFilter filter = new LoginAuthenticationFilter(authenticationManager, loginSuccessHandler);
        filter.setFilterProcessesUrl("/login");
        return filter;
    }
}
