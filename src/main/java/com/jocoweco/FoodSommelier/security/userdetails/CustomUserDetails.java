package com.jocoweco.FoodSommelier.security.userdetails;

import com.jocoweco.FoodSommelier.constant.LoginType;
import com.jocoweco.FoodSommelier.constant.Role;
import com.jocoweco.FoodSommelier.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final String uuid;
    private final String username;
    private final String password;
    private final Role role;
    private final LoginType loginType;
    private final Boolean isActive;


    public static CustomUserDetails fromUser(User user, String username, String password) {
        return new CustomUserDetails(
                user.getUuid(),
                username,
                password,
                user.getRole(),
                user.getLoginType(),
                user.isActive()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return uuid.toString();
    }

    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠김 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 사용 여부
    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
