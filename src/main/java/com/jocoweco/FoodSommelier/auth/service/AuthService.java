package com.jocoweco.FoodSommelier.auth.service;

import com.jocoweco.FoodSommelier.auth.domain.RefreshToken;
import com.jocoweco.FoodSommelier.auth.dto.LoginRequestDTO;
import com.jocoweco.FoodSommelier.auth.dto.RegisterRequestDTO;
import com.jocoweco.FoodSommelier.auth.dto.TokenRequestDTO;
import com.jocoweco.FoodSommelier.auth.dto.TokenResponseDTO;
import com.jocoweco.FoodSommelier.auth.repository.RefreshTokenRepository;
import com.jocoweco.FoodSommelier.constant.Role;
import com.jocoweco.FoodSommelier.security.jwt.JwtProvider;
import com.jocoweco.FoodSommelier.user.domain.User;
import com.jocoweco.FoodSommelier.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;

    /* 회원가입 */
    @Transactional
    public String createUser(RegisterRequestDTO request) {
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new RuntimeException("이미 가입된 아이디입니다.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        User user = User.builder()
                .userId(request.getUserId())
                .userPw(passwordEncoder.encode(request.getUserPw()))
                .nickname(request.getNickname())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return user.getUserId();
    }

    /* 로그인 */
    @Transactional
    public TokenResponseDTO login(LoginRequestDTO request) {
        // AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getUserId(), request.getUserPw());
        // 검증
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 사용자 정보 확인
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("UserId Not Found : " + request.getUserId()));

        // JWT 토큰 생성
        TokenResponseDTO token = jwtProvider.generateToken(authentication);

        // Redis에 refreshToken 저장
        refreshTokenRepository.save(new RefreshToken(user.getUserId(), token.getRefreshToken()));

        return token;
    }

    /* 토큰 재발급 */
    @Transactional
    public TokenResponseDTO reissueToken(TokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        String userId = jwtProvider.getUserId(refreshToken);

        // Redis에 RefreshToken 저장되어 있는지 확인
        RefreshToken foundTokenInfo = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("RefreshToken Not Found"));
        // 사용자 정보 확인
        User user = userRepository.findByUserId(foundTokenInfo.getUserId())
                .orElseThrow(() -> new RuntimeException("UserId Not Found : " + foundTokenInfo.getUserId()));
        // 유효성 검사
        jwtProvider.validateToken(refreshToken);

        // 인증 정보 가져오기
        Authentication authentication = jwtProvider.getAuthentication(refreshToken);
        // 새로운 JWT 토큰 생성
        TokenResponseDTO newToken = jwtProvider.generateToken(authentication);
        // Redis에 저장된 이전 토큰 제거
        refreshTokenRepository.deleteByUserId(user.getUserId());
        // Redis에 refreshToken 저장
        refreshTokenRepository.save(new RefreshToken(user.getUserId(), newToken.getRefreshToken()));

        return newToken;
    }

    /* 로그아웃 */
    @Transactional
    public void logout(String accessToken) {

        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization Header");
        }
        String token = accessToken.substring("Bearer ".length());

        String userId = jwtProvider.getUserId(token);
        if (userId == null) {
            throw new UsernameNotFoundException("UserId not found");
        }

        // Redis에서 refreshToken 삭제
        refreshTokenRepository.deleteById(userId);

        // Security Context 초기화
        SecurityContextHolder.clearContext();
    }
}
