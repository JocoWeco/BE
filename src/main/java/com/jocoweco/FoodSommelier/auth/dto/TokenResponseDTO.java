package com.jocoweco.FoodSommelier.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDTO {
    private String userId;
    private String accessToken;
    private String refreshToken;
}
