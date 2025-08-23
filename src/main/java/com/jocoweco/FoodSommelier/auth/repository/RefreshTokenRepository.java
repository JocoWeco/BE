package com.jocoweco.FoodSommelier.auth.repository;

import com.jocoweco.FoodSommelier.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    void deleteByUserId(String uuid);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
