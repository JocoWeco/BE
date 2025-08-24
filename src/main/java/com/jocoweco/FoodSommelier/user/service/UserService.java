package com.jocoweco.FoodSommelier.user.service;

import com.jocoweco.FoodSommelier.constant.LoginType;
import com.jocoweco.FoodSommelier.user.domain.LocalUser;
import com.jocoweco.FoodSommelier.user.domain.SocialUser;
import com.jocoweco.FoodSommelier.user.domain.User;
import com.jocoweco.FoodSommelier.user.dto.DeleteUserRequestDTO;
import com.jocoweco.FoodSommelier.user.repository.LocalUserRepository;
import com.jocoweco.FoodSommelier.user.repository.SocialUserRepository;
import com.jocoweco.FoodSommelier.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LocalUserRepository localUserRepository;
    private final SocialUserRepository socialUserRepository;

    private final PasswordEncoder passwordEncoder;


    /* 탈퇴 */
    @Transactional
    public void deleteUser(DeleteUserRequestDTO request) {
        String uuid = request.getUuid();

        // DB 데이터 제거
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        try {
            if (user.getLoginType() == LoginType.LOCAL) {
                LocalUser localUser = localUserRepository.findByUser(user)
                        .orElseThrow(() -> new UsernameNotFoundException("LocalUser Not Found"));
                if (!passwordEncoder.matches(request.getUserPw(), localUser.getPassword())) {
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
}