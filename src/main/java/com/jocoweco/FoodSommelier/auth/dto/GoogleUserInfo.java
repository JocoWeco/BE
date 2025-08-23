package com.jocoweco.FoodSommelier.auth.dto;

import com.jocoweco.FoodSommelier.constant.LoginType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GoogleUserInfo implements OAuth2UserInfo {
    private LoginType provider;
    private String providerId;
    private String email;
    private String name;

    @Override
    public LoginType getProvider() {
        return LoginType.GOOGLE;
    }

    @Override
    public String getProviderId() {
        return providerId;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

}
