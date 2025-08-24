package com.jocoweco.FoodSommelier.auth.controller;

import com.jocoweco.FoodSommelier.auth.dto.*;
import com.jocoweco.FoodSommelier.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /* 회원가입 */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterLocalRequestDTO request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok().body(authService.createUser(request) + " : 가입 성공");
    }

    /* 아이디 확인 */
    @PostMapping("/register/check-id")
    public ResponseEntity<Boolean> checkDuplicatedId(@RequestBody CheckLoginIdDTO request) {
        if (authService.isDuplicatedId(request.getLoginId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(false); // 사용 불가능
        }
        return ResponseEntity.ok(true); // 사용 가능
    }

    /* 닉네임 확인 */
    @PostMapping("/register/check-nickname")
    public ResponseEntity<Boolean> checkDuplicatedNickname(@RequestBody CheckNicknameRequestDTO request) {
        if (authService.isDuplicatedNickname(request.getNickname())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(false); // 사용 불가능
        }
        return ResponseEntity.ok(true); // 사용 가능
    }

    /* 이메일 확인 */
    @PostMapping("/register/check-email")
    public ResponseEntity<Boolean> checkDuplicatedEmail(@RequestBody CheckEmailRequestDTO request) {
        if (authService.isDuplicatedEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(false); // 사용 불가능
        }
        return ResponseEntity.ok(true); // 사용 가능
    }

    /* 로그인 */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok().body(authService.login(request));
    }

    /* 토큰 재발급 */
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDTO> reissue(@RequestBody TokenRequestDTO request) {
        return ResponseEntity.ok().body(authService.reissueToken(request));
    }

}
