package com.jocoweco.FoodSommelier.controller;

import com.jocoweco.FoodSommelier.service.UserService;
import com.jocoweco.FoodSommelier.dto.UserRequestDto;
import com.jocoweco.FoodSommelier.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping ("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 유저 정보 수정
    @PatchMapping("/update")
    public ResponseEntity<UserResponseDto> updateUser(@RequestBody UserRequestDto userReq) {
        UserResponseDto userResponseDto = userService.updateUser(userReq);
        return ResponseEntity.ok(userResponseDto);
    }
}
