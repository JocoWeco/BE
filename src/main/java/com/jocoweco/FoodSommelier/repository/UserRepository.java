package com.jocoweco.FoodSommelier.repository;

import com.jocoweco.FoodSommelier.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickName(String nickName);
    Optional<User> findByUserId(String userId);
    Optional<User> findBySavedStore(String savedStore);
}
