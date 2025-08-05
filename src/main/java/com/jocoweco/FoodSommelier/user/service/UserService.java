package com.jocoweco.FoodSommelier.user.service;

import com.jocoweco.FoodSommelier.user.domain.User;
import com.jocoweco.FoodSommelier.user.dto.DeleteUserRequestDTO;
import com.jocoweco.FoodSommelier.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    /* 탈퇴 */
    @Transactional
    public void deleteUser(DeleteUserRequestDTO request) {

        // DB 데이터 제거
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        if (!passwordEncoder.matches(request.getUserPw(), user.getUserPw())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        userRepository.delete(user);
    }
}
