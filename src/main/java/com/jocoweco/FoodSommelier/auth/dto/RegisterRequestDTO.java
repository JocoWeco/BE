package com.jocoweco.FoodSommelier.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequestDTO {
    private String userId;
    private String userPw;
    private String nickname;
}
