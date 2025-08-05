package com.jocoweco.FoodSommelier.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 3600 * 24 * 7) // 7일
public class RefreshToken {
    @Id
    private String userId;

    private String refreshToken;
}
