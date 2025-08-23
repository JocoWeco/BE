package com.jocoweco.FoodSommelier.security.userdetails;

import com.jocoweco.FoodSommelier.constant.LoginType;
import com.jocoweco.FoodSommelier.user.repository.LocalUserRepository;
import com.jocoweco.FoodSommelier.user.repository.SocialUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final LocalUserRepository localUserRepository;
    private final SocialUserRepository socialUserRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // 일반 로그인
        return localUserRepository.findByLocalId(identifier)
                .map(localUser -> new CustomUserDetails(
                        localUser.getUser().getUuid(),
                        identifier,
                        localUser.getPassword(),
                        localUser.getUser().getRole(),
                        LoginType.LOCAL,
                        localUser.getUser().isActive()
                )).or(() ->
                        // 소셜 로그인
                        socialUserRepository.findByProviderId(identifier)
                                .map(socialUser -> new CustomUserDetails(
                                        socialUser.getUser().getUuid(),
                                        identifier,
                                        null,
                                        socialUser.getUser().getRole(),
                                        LoginType.GOOGLE,
                                        socialUser.getUser().isActive()
                                )))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
