package com.jocoweco.FoodSommelier.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequestDTO {
    private String refreshToken;
}
