package com.jocoweco.FoodSommelier.security.jwt;

/*
 * JWT 토근과 관련된 암호화, 복호화, 검증
 * */

import com.jocoweco.FoodSommelier.auth.dto.TokenResponseDTO;
import com.jocoweco.FoodSommelier.security.userdetails.CustomUserDetails;
import com.jocoweco.FoodSommelier.user.domain.LocalUser;
import com.jocoweco.FoodSommelier.user.domain.SocialUser;
import com.jocoweco.FoodSommelier.user.domain.User;
import com.jocoweco.FoodSommelier.user.repository.LocalUserRepository;
import com.jocoweco.FoodSommelier.user.repository.SocialUserRepository;
import com.jocoweco.FoodSommelier.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 7일

    private final SecretKey key;
    private final UserRepository userRepository;
    private final LocalUserRepository localUserRepository;
    private final SocialUserRepository socialUserRepository;


    public JwtProvider(@Value("${jwt.secret.key}") String secretKey, UserRepository userRepository, LocalUserRepository localUserRepository, SocialUserRepository socialUserRepository) {
        this.userRepository = userRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.localUserRepository = localUserRepository;
        this.socialUserRepository = socialUserRepository;
    }

    /* 토큰 생성 */
    public TokenResponseDTO generateToken(Authentication authentication) {

        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 만료일 설정
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        // Access Token 생성
        String accessToken = Jwts.builder()
                .claim(AUTHORITIES_KEY, authorities) // 권한
                .subject(authentication.getName()) // 사용자 uuid
                .expiration(accessTokenExpiresIn) // 만료일
                .signWith(this.key)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .claim(AUTHORITIES_KEY, authorities) // 권한
                .subject(authentication.getName()) // 사용자 uuid
                .expiration(refreshTokenExpiresIn) // 만료일
                .signWith(this.key)
                .compact();

        return TokenResponseDTO.builder()
                .uuid(authentication.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /* AccessToken 복호화하여 사용자 인증 정보 조회
     * 토큰의 Claims에서 권한 정보 추출
     * User 객체를 생성해 Authentication 객체로 반환 */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        // 권한 정보 확인
        String authorities = claims.get(AUTHORITIES_KEY).toString();
        if (authorities == null || authorities.isEmpty()) {
            throw new AuthorizationDeniedException("권한 정보가 없는 토큰입니다.");
        }

        // DB 정보 확인
        String uuidString = claims.getSubject();

        User user = userRepository.findByUuid(uuidString)
                .orElseThrow(() -> new UsernameNotFoundException("유저 정보를 찾을 수 없습니다."));

        String username;
        String password;

        switch (user.getLoginType()) {
            case LOCAL -> {
                LocalUser localUser = localUserRepository.findByUser(user)
                        .orElseThrow(() -> new UsernameNotFoundException("Local 사용자 정보를 찾을 수 없습니다."));
                username = localUser.getLocalId();
                password = localUser.getPassword();
            }
            case GOOGLE -> {
                SocialUser socialUser = socialUserRepository.findByUser(user)
                        .orElseThrow(() -> new UsernameNotFoundException("Social 사용자 정보를 찾을 수 없습니다."));
                username = socialUser.getProviderId();
                password = null;
            }
            default -> throw new UsernameNotFoundException("지원되지 않는 로그인 유형입니다.");
        }

        UserDetails principal = CustomUserDetails.fromUser(user, username, password);

        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    /* 토큰 사용자 조회 */
    public String getUuid(String accessToken) {
        try {
            Claims claims = parseClaims(accessToken);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /* 토큰 유효성 검사 */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /* Claims 복호화 */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}

