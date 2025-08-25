package com.jocoweco.FoodSommelier.ai.service;

import com.jocoweco.FoodSommelier.ai.dto.OrderRequestDTO;
import com.jocoweco.FoodSommelier.security.jwt.JwtProvider;
import com.jocoweco.FoodSommelier.user.domain.User;
import com.jocoweco.FoodSommelier.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {
    private JwtProvider jwtProvider;
    private UserRepository userRepository;

    public String makeSendMessage(String token, OrderRequestDTO request) {
        String uuid = jwtProvider.getUuid(token.substring(7));
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.info(uuid);
        log.info(user.getEmail());

        Double lat = request.getLat();
        Double lng = request.getLng();
        String currentLocation = ", 현재 위치 lat:" + lat + ", lng:" + lng;
        log.info(currentLocation);

        String excludedIngregient = "";
        if (user.getExcludedIngredient() != null) {
            excludedIngregient = user.getExcludedIngredient();
            log.info(excludedIngregient);
        }
        String message = request.getMassage();
        log.info(message);

        return message + currentLocation + ", 제외 재료: " + excludedIngregient;
    }
}
