package com.jocoweco.FoodSommelier.auth.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDTO {

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;
}
