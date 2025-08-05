package com.jocoweco.FoodSommelier.user.repository;

import com.jocoweco.FoodSommelier.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUserId(String userId);

    boolean existsByNickname(String nickname);

    Optional<User> findByUserId(String userId);
}
