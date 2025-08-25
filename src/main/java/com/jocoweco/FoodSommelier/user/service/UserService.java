package com.jocoweco.FoodSommelier.user.service;

import com.jocoweco.FoodSommelier.constant.LoginType;
import com.jocoweco.FoodSommelier.security.jwt.JwtProvider;
import com.jocoweco.FoodSommelier.user.domain.LocalUser;
import com.jocoweco.FoodSommelier.user.domain.SocialUser;
import com.jocoweco.FoodSommelier.user.domain.User;
import com.jocoweco.FoodSommelier.user.dto.UserInfoDTO;
import com.jocoweco.FoodSommelier.user.repository.LocalUserRepository;
import com.jocoweco.FoodSommelier.user.repository.SocialUserRepository;
import com.jocoweco.FoodSommelier.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LocalUserRepository localUserRepository;
    private final SocialUserRepository socialUserRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /* 정보 조회 */
    public UserInfoDTO getUserInfoDTO(String token) {
        User user = getDataFromToken(token);

        return UserInfoDTO.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .birth(user.getBirth())
                .gender(user.getGender())
                .excludedIngredient(user.getExcludedIngredient())
                .build();
    }

    /* 정보 수정 */
//    @Transactional
//    public UserInfoDTO updateUserInfo(UserInfoDTO userInfo) {
//
//    }

    /* 제외 재료 수정 */


    /* 탈퇴 */
    @Transactional
    public void deleteUser(String token, String password) {

        String uuid = jwtProvider.getUuid(token);

        // DB 데이터 제거
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        try {
            if (user.getLoginType() == LoginType.LOCAL) {
                LocalUser localUser = localUserRepository.findByUser(user)
                        .orElseThrow(() -> new UsernameNotFoundException("LocalUser Not Found"));
                if (!passwordEncoder.matches(password, localUser.getPassword())) {
                    throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
                }
                localUserRepository.delete(localUser);
                userRepository.delete(user);

            } else if (user.getLoginType() == LoginType.GOOGLE) {
                SocialUser socialUser = socialUserRepository.findByUser(user)
                        .orElseThrow(() -> new UsernameNotFoundException("Google User Not Found"));
                socialUserRepository.delete(socialUser);
                userRepository.delete(user);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 토큰에서 데이터 추출
    private User getDataFromToken(String token) {
        String uuid = jwtProvider.getUuid(token);

        if (uuid == null) {
            throw new UsernameNotFoundException("Invalid token");
        }

        return userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

    }

    // 토큰 유효성 검사
    public ResponseEntity<?> getUserInfoResponse(String token) {

        if (!jwtProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        try {
            UserInfoDTO userInfoDTO = getUserInfoDTO(token);
            return ResponseEntity.ok(userInfoDTO);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 정보를 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
        }
    }

    /* 비밀번호 확인 */
//    public boolean isCorrespond(String password) {
//        
//    }
}