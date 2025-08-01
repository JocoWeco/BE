package com.jocoweco.FoodSommelier.dto;

import com.jocoweco.FoodSommelier.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    @NonNull
    private String userId;
    @NonNull
    private String nickName;
    private String recently_store;
    private String saved_store;


    // 엔티티 -> dto 변환 메서드
    public static UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getUserId(),
                user.getNickName(),
                user.getRecently_store(),
                user.getSaved_store()
        );
    }
}
