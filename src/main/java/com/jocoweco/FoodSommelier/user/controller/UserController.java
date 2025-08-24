package com.jocoweco.FoodSommelier.user.controller;

import com.jocoweco.FoodSommelier.auth.service.AuthService;
import com.jocoweco.FoodSommelier.user.dto.DeleteUserRequestDTO;
import com.jocoweco.FoodSommelier.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    /* test */
    @GetMapping("/{uid}")
    public ResponseEntity<String> getUser(@PathVariable String uid) {
        return ResponseEntity.ok().body(uid + ": 로그인 완료");
    }

    /* 로그아웃 */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken) {
        try {
            authService.logout(accessToken);
            return ResponseEntity.ok("로그아웃 완료");
        } catch (IllegalArgumentException | UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("로그아웃 실패");
        }
    }

    /* 탈퇴 */
    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody DeleteUserRequestDTO request, @RequestHeader("Authorization") String accessToken) {
        try {
            authService.logout(accessToken);
            userService.deleteUser(request);
            return ResponseEntity.ok().body("탈퇴 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("탈퇴 실패");
        }
    }
}
