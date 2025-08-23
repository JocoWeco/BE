package com.jocoweco.FoodSommelier.auth.dto;

import com.jocoweco.FoodSommelier.constant.LoginType;

public interface OAuth2UserInfo {
    LoginType getProvider();

    String getProviderId();

    String getEmail();

    String getName();
}
