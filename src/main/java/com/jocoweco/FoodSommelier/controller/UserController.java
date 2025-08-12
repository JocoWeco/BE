package com.jocoweco.FoodSommelier.controller;

import com.jocoweco.FoodSommelier.dto.UserRequestDto;
import com.jocoweco.FoodSommelier.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping ("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 유저 정보 수정
    @PatchMapping("/update")
    public ResponseEntity<Void> updateUser(@RequestBody UserRequestDto userReq) {
        userService.updateUser(userReq);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/selectStore")
//    public ResponseEntity<StoreResponseDto> selectStore(@RequestBody storeRequestDto userReq) {
//
//    }
}
