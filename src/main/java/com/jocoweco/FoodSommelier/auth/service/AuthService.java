package com.jocoweco.FoodSommelier.auth.service;

import com.jocoweco.FoodSommelier.auth.domain.RefreshToken;
import com.jocoweco.FoodSommelier.auth.dto.LoginRequestDTO;
import com.jocoweco.FoodSommelier.auth.dto.RegisterLocalRequestDTO;
import com.jocoweco.FoodSommelier.auth.dto.TokenRequestDTO;
import com.jocoweco.FoodSommelier.auth.dto.TokenResponseDTO;
import com.jocoweco.FoodSommelier.auth.repository.RefreshTokenRepository;
import com.jocoweco.FoodSommelier.constant.LoginType;
import com.jocoweco.FoodSommelier.constant.Role;
import com.jocoweco.FoodSommelier.security.jwt.JwtProvider;
import com.jocoweco.FoodSommelier.security.userdetails.CustomUserDetails;
import com.jocoweco.FoodSommelier.user.domain.LocalUser;
import com.jocoweco.FoodSommelier.user.domain.User;
import com.jocoweco.FoodSommelier.user.repository.LocalUserRepository;
import com.jocoweco.FoodSommelier.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final LocalUserRepository localUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;

    /* 회원가입 */
    @Transactional
    public String createUser(RegisterLocalRequestDTO request) {

        // 아이디 사용 가능 확인
        if (!request.isEnabledNickname())
            throw new IllegalArgumentException("사용 불가능한 아이디입니다.");

        // 사용한 메일인지 확인
        if (!request.isEnabledEmail()) {
            throw new IllegalArgumentException("이미 가입한 메일입니다..");
        }

        // 닉네임 사용 가능 확인
        if (!request.isEnabledLoginId())
            throw new IllegalArgumentException("사용 불가능한 닉네임입니다.");

        User user = User.builder()
                .uuid(generateUuid())
                .loginType(LoginType.LOCAL)
                .nickname(request.getNickname())
                .email(request.getEmail())
                .gender(request.getGender())
                .role(Role.USER)
                .isActive(true)
                .build();
        userRepository.save(user);

        LocalUser localUser = LocalUser.builder()
                .localId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .user(user)
                .build();
        localUserRepository.save(localUser);

        return user.getNickname();
    }

    // UUID 생성
    public String generateUuid() {
        return UUID.randomUUID().toString();
    }

    // 동일 아이디 여부 확인
    public boolean isDuplicatedId(String loginId) {
        return localUserRepository.existsByLocalId(loginId);
    }

    // 동일 닉네임 여부 확인
    public boolean isDuplicatedNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
    // 동일 이메일 여부 확인
    public boolean isDuplicatedEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /* 로그인 */
    @Transactional
    public TokenResponseDTO login(LoginRequestDTO request) {

        // AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword());

        // 인증
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // JWT 토큰 생성
        TokenResponseDTO token = jwtProvider.generateToken(authentication);

        // 인증된 유저 UUID 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String uuid = userDetails.getUuid();

        // Redis에 refreshToken 저장
        refreshTokenRepository.save(new RefreshToken(uuid, token.getRefreshToken()));

        return token;
    }

    /* 토큰 재발급 */
    @Transactional
    public TokenResponseDTO reissueToken(TokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        String uuid = jwtProvider.getUuid(refreshToken);

        // Redis에 RefreshToken 저장되어 있는지 확인
        RefreshToken foundTokenInfo = refreshTokenRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("RefreshToken Not Found"));
       
        // 사용자 정보 확인
        User user = userRepository.findByUuid(foundTokenInfo.getUuid())
                .orElseThrow(() -> new RuntimeException("UserId Not Found : " + foundTokenInfo.getUuid()));
        // 유효성 검사
        jwtProvider.validateToken(refreshToken);

        // 인증 정보 가져오기
        Authentication authentication = jwtProvider.getAuthentication(refreshToken);
        // 새로운 JWT 토큰 생성
        TokenResponseDTO newToken = jwtProvider.generateToken(authentication);
        // Redis에 저장된 이전 토큰 제거
        refreshTokenRepository.deleteByUserId(user.getUuid());
        // Redis에 refreshToken 저장
        refreshTokenRepository.save(new RefreshToken(user.getUuid(), newToken.getRefreshToken()));

        return newToken;
    }

    /* 로그아웃 */
    @Transactional
    public void logout(String accessToken) {

        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization Header");
        }
        String token = accessToken.substring("Bearer ".length());

        String userId = jwtProvider.getUuid(token);
        if (userId == null) {
            throw new UsernameNotFoundException("회원 정보를 찾을 수 없습니다.");
        }

        // Redis에서 refreshToken 삭제
        refreshTokenRepository.deleteById(userId);

        // Security Context 초기화
        SecurityContextHolder.clearContext();
    }
}
