package com.jocoweco.FoodSommelier.auth.controller;

import com.jocoweco.FoodSommelier.auth.dto.LoginRequestDTO;
import com.jocoweco.FoodSommelier.auth.dto.RegisterRequestDTO;
import com.jocoweco.FoodSommelier.auth.dto.TokenRequestDTO;
import com.jocoweco.FoodSommelier.auth.dto.TokenResponseDTO;
import com.jocoweco.FoodSommelier.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /* 회원가입 */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.createUser(request) + " : 가입 성공");
    }

    /* 로그인 */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /* 토큰 재발급 */
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDTO> reissue(@RequestBody TokenRequestDTO request) {
        return ResponseEntity.ok(authService.reissueToken(request));
    }

}
