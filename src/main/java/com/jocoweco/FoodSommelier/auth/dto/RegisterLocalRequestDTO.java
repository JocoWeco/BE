package com.jocoweco.FoodSommelier.auth.dto;

import com.jocoweco.FoodSommelier.constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class RegisterLocalRequestDTO {
    private String loginId;
    private String password;

    private String nickname;
    private String email;
    private Gender gender;
    private LocalDate birth;

    private boolean enabledLoginId;
    private boolean enabledNickname;
    private boolean enabledEmail;
}
