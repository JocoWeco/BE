package com.jocoweco.FoodSommelier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// 스프링 시큐리티 때문에 회원수정 쿼리 보내는 부분이 까다로워서 개발중일때는 기능을 꺼놓고 테스트했습니다.
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable()); // CSRF 토큰 비활성화 (POST, PATCH 가능)
        return http.build();
    }
}
