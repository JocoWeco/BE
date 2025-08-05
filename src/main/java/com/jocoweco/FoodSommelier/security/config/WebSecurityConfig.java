package com.jocoweco.FoodSommelier.security.config;

import com.jocoweco.FoodSommelier.security.jwt.JwtAccessDeniedHandler;
import com.jocoweco.FoodSommelier.security.jwt.JwtAuthenticationEntryPoint;
import com.jocoweco.FoodSommelier.security.jwt.JwtFilter;
import com.jocoweco.FoodSommelier.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // csrf 설정 : disable
                .csrf(AbstractHttpConfigurer::disable)

                // Http Basic 인증 : disable
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션 설정
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 예외 처리
                .exceptionHandling((exception) -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 예외
                        .accessDeniedHandler(jwtAccessDeniedHandler)) // 인가 예외

                // Form 로그인 : disable
                .formLogin(AbstractHttpConfigurer::disable)

                /* 경로별 인가 작업 */
                // 회원가입, 로그인 API는 토큰 없는 상태에서 요청 들어옴
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated())

                // JwtProvider, JwtFilter 적용
                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)

                .cors(Customizer.withDefaults());

        return http.build();
    }

    /* 로그아웃 */
    private void configureLogout(LogoutConfigurer<HttpSecurity> logoutConfigurer) {

    }

}
