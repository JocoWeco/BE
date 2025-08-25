package com.jocoweco.FoodSommelier.user.repository;

import com.jocoweco.FoodSommelier.constant.LoginType;
import com.jocoweco.FoodSommelier.user.domain.SocialUser;
import com.jocoweco.FoodSommelier.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {

    Optional<SocialUser> findByProviderId(String providerId);

    Optional<SocialUser> findByProviderAndProviderId(LoginType provider, String providerId);

    Optional<SocialUser> findByUserUid(Long userUid);

    Optional<SocialUser> findByUser(User user);
}
