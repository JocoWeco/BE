package com.jocoweco.FoodSommelier.user.repository;

import com.jocoweco.FoodSommelier.user.domain.LocalUser;
import com.jocoweco.FoodSommelier.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalUserRepository extends JpaRepository<LocalUser, Long> {

    boolean existsByLocalId(String localId);

    Optional<LocalUser> findByLocalId(String localId);

    Optional<LocalUser> findByUserUid(Long userUid);

    Optional<LocalUser> findByUser(User user);
}
