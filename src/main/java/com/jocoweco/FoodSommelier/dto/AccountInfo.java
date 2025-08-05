package com.jocoweco.FoodSommelier.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class AccountInfo {
    private String userId;
    private String email;
    private boolean emailVerified;
}
