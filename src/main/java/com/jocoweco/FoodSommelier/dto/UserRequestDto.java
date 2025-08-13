package com.jocoweco.FoodSommelier.dto;

import jakarta.persistence.Column;
import lombok.Getter;

@Getter
public class UserRequestDto {
    @Column(nullable = false)
    private Long uid;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String nickName;
    @Column(nullable = false)
    private String user_pw;
}
