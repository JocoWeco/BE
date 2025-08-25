package com.jocoweco.FoodSommelier.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.jocoweco.FoodSommelier.auth.domain.RefreshToken;
import com.jocoweco.FoodSommelier.auth.dto.GoogleIdTokenRequestDTO;
import com.jocoweco.FoodSommelier.auth.dto.GoogleUserInfo;
import com.jocoweco.FoodSommelier.auth.dto.RegisterSocialRequestDTO;
import com.jocoweco.FoodSommelier.auth.dto.TokenResponseDTO;
import com.jocoweco.FoodSommelier.auth.repository.RefreshTokenRepository;
import com.jocoweco.FoodSommelier.constant.LoginType;
import com.jocoweco.FoodSommelier.constant.Role;
import com.jocoweco.FoodSommelier.security.jwt.JwtProvider;
import com.jocoweco.FoodSommelier.security.userdetails.CustomUserDetails;
import com.jocoweco.FoodSommelier.user.domain.SocialUser;
import com.jocoweco.FoodSommelier.user.domain.User;
import com.jocoweco.FoodSommelier.user.repository.SocialUserRepository;
import com.jocoweco.FoodSommelier.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final SocialUserRepository socialUserRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;

    private final HttpTransport httpTransport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    @Value("${google.client.client-id}")
    private String googleClientId;

    /* 구글 로그인 */
    public TokenResponseDTO googleLogin(GoogleIdTokenRequestDTO request) throws GeneralSecurityException {

        String idToken = request.getGoogleIdToken();

        // Google 토큰 검증
        GoogleUserInfo googleUserInfo = verifyIdToken(idToken);
        LoginType loginType = googleUserInfo.getProvider();

        // 유저 정보 확인
        Optional<SocialUser> socialUserOpt = socialUserRepository.findByProviderAndProviderId(loginType, googleUserInfo.getProviderId());
        if (socialUserOpt.isEmpty()) {
            return null;
        }

        // 유저 정보 있는 경우
        SocialUser socialUser = socialUserOpt.get();
        CustomUserDetails userDetails = CustomUserDetails.fromUser(socialUser.getUser(), socialUser.getProviderId(), null);

        // 인증
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // JWT 토큰 생성
        TokenResponseDTO token = jwtProvider.generateToken(authentication);

        // Redis에 토큰 저장
        refreshTokenRepository.save(new RefreshToken(socialUser.getUser().getUuid(), token.getRefreshToken()));

        return token;
    }

    /* 구글 회원가입 */
    public TokenResponseDTO googleRegister(RegisterSocialRequestDTO request) {
        User user = User.builder()
                .uuid(generateUuid())
                .loginType(LoginType.GOOGLE)
                .nickname(request.getNickname())
                .gender(request.getGender())
                .role(Role.USER)
                .isActive(true)
                .build();
        userRepository.save(user);

        SocialUser socialUser = SocialUser.builder()
                .providerId(request.getProviderId())
                .provider(LoginType.GOOGLE)
                .name(request.getName())
                .build();
        socialUserRepository.save(socialUser);

        // 토큰 생성
        CustomUserDetails userDetails = CustomUserDetails.fromUser(user, request.getProviderId(), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        TokenResponseDTO token = jwtProvider.generateToken(authentication);

        refreshTokenRepository.save(new RefreshToken(user.getUuid(), token.getRefreshToken()));
        return token;
    }

    // Google ID Token 검증
    public GoogleUserInfo verifyIdToken(String idToken) throws GeneralSecurityException {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken != null) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();

                String providerId = payload.getSubject();
                String email = payload.getEmail();
                String name = Optional.ofNullable((String) payload.get("name")).orElse("Unknown");

                return GoogleUserInfo.builder()
                        .providerId(providerId)
                        .email(email)
                        .name(name).build();

            } else {
                throw new IllegalArgumentException("Invalid ID token");
            }
        } catch (Exception e) {
            throw new GeneralSecurityException("Failed to authenticate Google IDToken", e);
        }
    }

    // UUID 생성
    public String generateUuid() {
        return UUID.randomUUID().toString();
    }

    // 동일 닉네임 여부 확인
    public boolean isDuplicatedNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
