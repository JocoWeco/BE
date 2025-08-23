package com.jocoweco.FoodSommelier.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteUserRequestDTO {
    private String uuid;
    private String userPw;
}
