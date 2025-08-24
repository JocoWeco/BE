package com.jocoweco.FoodSommelier.user.controller;

import com.jocoweco.FoodSommelier.auth.service.AuthService;
import com.jocoweco.FoodSommelier.security.jwt.JwtProvider;
import com.jocoweco.FoodSommelier.user.dto.CheckPasswordRequestDTO;
import com.jocoweco.FoodSommelier.user.dto.ExcludedIngredientDTO;
import com.jocoweco.FoodSommelier.user.dto.UserInfoDTO;
import com.jocoweco.FoodSommelier.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    /* test */
    @GetMapping("/{uid}")
    public ResponseEntity<String> getUser(@PathVariable String uid) {
        return ResponseEntity.ok().body(uid + ": 로그인 완료");
    }

    /* 정보 조회 */
    @PostMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        return userService.getUserInfoResponse(token);
    }

    /* 정보 수정 */
//    @PutMapping("/update")
//    public ResponseEntity<UserInfoDTO> updateUser(@RequestHeader("Authorization") String token, @RequestBody UserInfoDTO request) {
//
//    }

    /* 제외 재료 수정 */
//    @PatchMapping("/excluded-gredient")
//    public ResponseEntity<ExcludedIngredientDTO> updateExcludedIngredient(@RequestHeader("Authorization") String token, @RequestBody ExcludedIngredientDTO request) {
//
//    }

    /* 로그아웃 */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken) {
        try {
            authService.logout(accessToken);
            return ResponseEntity.ok().body("로그아웃 완료");
        } catch (IllegalArgumentException | UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("로그아웃 실패");
        }
    }

    /* 탈퇴 */
    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestHeader("Authorization") String accessToken, @RequestBody CheckPasswordRequestDTO request) {
        try {
            authService.logout(accessToken);
            userService.deleteUser(accessToken, request.getPassword());
            return ResponseEntity.ok().body("탈퇴 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("탈퇴 실패");
        }
    }

    /* 비밀번호 확인 */
//    @PostMapping("/check-password")
//    public ResponseEntity<Boolean> checkPassword(@RequestHeader("Authorization") String accessToken, @RequestBody CheckPasswordRequestDTO request) {
//        if(!userService.isCorrespond(request.getPassword())){
//            return ResponseEntity.badRequest().body(false);
//        }
//
//    }
}
