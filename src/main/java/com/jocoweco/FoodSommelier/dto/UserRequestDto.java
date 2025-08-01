package com.jocoweco.FoodSommelier.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    @NonNull
    private Long uid;
    @NonNull
    private String userId;
    @NonNull
    private String nickName;
    @NonNull
    private String user_pw;
    private String recently_store;
    private String saved_store;
}
