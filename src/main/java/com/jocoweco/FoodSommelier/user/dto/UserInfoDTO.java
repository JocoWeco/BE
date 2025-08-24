package com.jocoweco.FoodSommelier.user.dto;

import com.jocoweco.FoodSommelier.constant.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UserInfoDTO {
    private String nickname;
    private String email;
    private Gender gender;
    private LocalDate birth;
    private String excludedIngredient;

}
