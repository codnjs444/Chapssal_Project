package com.chapssal.message.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/ws/**", "/assets/**").permitAll() // 홈, 인덱스 및 WebSocket 엔드포인트 접근 허용
                        .anyRequest().permitAll() // 다른 모든 요청은 인증 필요
                )
                .formLogin(form -> form
                        .loginPage("/login") // 로그인 페이지 설정
                        .permitAll() // 로그인 페이지 접근 허용
                )
                .logout(logout -> logout.permitAll()); // 로그아웃 접근 허용

        return http.build();
    }
}