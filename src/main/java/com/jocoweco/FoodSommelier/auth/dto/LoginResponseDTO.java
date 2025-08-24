package com.jocoweco.FoodSommelier.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginResponseDTO {
    private String uuid;
    private String nickname;
    private String accessToken;
    private String refreshToken;
}
