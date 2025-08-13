package com.jocoweco.FoodSommelier.service;

import com.jocoweco.FoodSommelier.domain.User;
import com.jocoweco.FoodSommelier.dto.UserRequestDto;
import com.jocoweco.FoodSommelier.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 유저 정보 수정 (요청 Dto 입력 -> 응답 dto 반환)
    @Transactional // 자동 커밋
    public void updateUser(UserRequestDto userReq) {
        // 기본키
        User user = userRepository
                .findById(userReq.getUid())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음")); // 기본키 조회 불가시

        // 공백 체크
        if (userReq.getUserId().equals("")) throw new IllegalArgumentException("아이디 값이 공백");
        if (userReq.getNickName().equals("")) throw new IllegalArgumentException("닉네임 값이 공백");
        if (userReq.getUser_pw().equals("")) throw new IllegalArgumentException("비밀번호 값이 공백");

        // 회원 아이디, 닉네임 중복 체크 로직 작성 (중복 시 )//
        checkDuplicateNickName(user, userReq.getNickName());
        checkDuplicateUserId(user, userReq.getUserId());

        // 정보 수정
        user.updateUser(
                userReq.getUserId(),
                userReq.getNickName(),
                userReq.getUser_pw()
        );
    }

    // 닉네임 중복 체크
    public void checkDuplicateNickName(User user, String nickName) {
        if (!user.getNickName().equals(nickName)) {
            // 닉네임이 조회가 되는 경우만 람다 실행
            userRepository
                    .findByNickName(nickName)
                    .ifPresent(user1 -> {throw new DuplicateKeyException("이미 존재하는 닉네임");});
        }
    }

    // 유저 아이디 중복 체크
    public void checkDuplicateUserId(User user, String userId) {
        if (!user.getUserId().equals(userId)) {
            // 아이디가 조회가 되는 경우만 람다 실행
            userRepository
                    .findByUserId(userId)
                    .ifPresent(user1 -> {throw new DuplicateKeyException("이미 존재하는 아이디");});
        }
    }
}