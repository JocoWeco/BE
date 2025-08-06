package com.jocoweco.FoodSommelier.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.jocoweco.FoodSommelier.dto.AccountInfo;
import com.jocoweco.FoodSommelier.dto.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class GoogleAuthController {

    @PostMapping
    public ResponseEntity<AccountInfo> authenticate(@RequestBody TokenInfo tokenInfo) throws Exception {
        FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(tokenInfo.getIdToken());

        AccountInfo accountInfo = AccountInfo.builder()
                .userId(token.getUid())
                .email(token.getEmail())
                .emailVerified(token.isEmailVerified())
                .build();

        return ResponseEntity.ok(accountInfo);
    }
}
