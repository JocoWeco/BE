package com.jocoweco.FoodSommelier.auth.controller;

import com.jocoweco.FoodSommelier.auth.dto.CheckNicknameRequestDTO;
import com.jocoweco.FoodSommelier.auth.dto.GoogleIdTokenRequestDTO;
import com.jocoweco.FoodSommelier.auth.dto.RegisterSocialRequestDTO;
import com.jocoweco.FoodSommelier.auth.dto.TokenResponseDTO;
import com.jocoweco.FoodSommelier.auth.service.GoogleAuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/auth/google")
@AllArgsConstructor
public class GoogleAuthController {

    private GoogleAuthService googleAuthService;

    /* 구글 로그인 */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> googleLogin(@RequestBody GoogleIdTokenRequestDTO request) throws GeneralSecurityException {
        TokenResponseDTO result = googleAuthService.googleLogin(request);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok().body(result);
    }

    /* 구글 회원가입 */
    @PostMapping("/register")
    public ResponseEntity<TokenResponseDTO> googleRegister(@RequestBody RegisterSocialRequestDTO request) {
        return ResponseEntity.ok().body(googleAuthService.googleRegister(request));
    }

    /* 닉네임 확인 */
    @PostMapping("/register/check-nickname")
    public ResponseEntity<Boolean> checkDuplicateNickname(@RequestBody CheckNicknameRequestDTO request) {
        if (googleAuthService.isDuplicatedNickname(request.getNickname())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(false); // 사용 불가능
        }
        return ResponseEntity.ok(true); // 사용 가능
    }

}
