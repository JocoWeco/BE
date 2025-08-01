package com.jocoweco.FoodSommelier.service;

import com.jocoweco.FoodSommelier.domain.User;
import com.jocoweco.FoodSommelier.dto.UserRequestDto;
import com.jocoweco.FoodSommelier.dto.UserResponseDto;
import com.jocoweco.FoodSommelier.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 유저 정보 수정 (요청 Dto 입력 -> 응답 dto 반환)
    @Transactional
    public UserResponseDto updateUser(UserRequestDto userReq) {
        User user = userRepository
                .findById(userReq.getUid())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음")); // 기본키 조회 불가시

        // 회원 아이디, 닉네임 중복 체크 로직 작성 //

        user.updateUser(
                userReq.getUserId(),
                userReq.getNickName(),
                userReq.getUser_pw(),
                userReq.getRecently_store(),
                userReq.getSaved_store()
        );

        return UserResponseDto.toUserResponseDto(user);
    }
}