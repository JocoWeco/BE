package com.jocoweco.FoodSommelier.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginRequestDTO {
    private String userId;
    private String userPw;
}
