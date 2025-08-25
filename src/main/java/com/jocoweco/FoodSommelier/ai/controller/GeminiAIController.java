package com.jocoweco.FoodSommelier.ai.controller;

import com.jocoweco.FoodSommelier.ai.dto.OrderRequestDTO;
import com.jocoweco.FoodSommelier.ai.service.GeminiAIService;
import com.jocoweco.FoodSommelier.ai.dto.OrderResponseDTO;
import com.jocoweco.FoodSommelier.ai.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class GeminiAIController {

    private final GeminiAIService geminiAiService;
    private final OrderService orderService;

    @PostMapping("/message")
    public ResponseEntity<OrderResponseDTO> summitOrder(@RequestHeader("Authorization") String token, @RequestBody OrderRequestDTO request) {
        try {

            String message = orderService.makeSendMessage(token, request);

            OrderResponseDTO response = geminiAiService.getResponseMessage(message);
            log.info(response.toString());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("주문서 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(null);
        }
    }
}
