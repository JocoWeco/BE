package com.jocoweco.FoodSommelier.security;

import io.jsonwebtoken.security.SecurityException;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
public class SecurityUtil {
    public static String getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new SecurityException("Security Context에 인증 정보가 없습니다.");
        }
        return authentication.getName();
    }
}
