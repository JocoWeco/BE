package com.jocoweco.FoodSommelier.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    @Column(nullable = false)
    private Long uid;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String nickName;
    @Column(nullable = false)
    private String user_pw;
    private String recentlyStore;
    private String savedStore;
}
