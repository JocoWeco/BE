package com.jocoweco.FoodSommelier.user.repository;

import com.jocoweco.FoodSommelier.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    Optional<User> findByUuid(String uuid);
    
}
