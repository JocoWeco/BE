package com.jocoweco.FoodSommelier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 안드로이드와의 연결을 위한 설정
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")  // 개발 중에는 * 가능, 배포 시 도메인 제한 필요
                        .allowedMethods("GET", "POST", "PATCH", "DELETE");
            }
        };
    }
}
