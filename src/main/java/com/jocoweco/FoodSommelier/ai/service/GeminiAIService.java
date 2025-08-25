package com.jocoweco.FoodSommelier.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocoweco.FoodSommelier.ai.dto.OrderResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GeminiAIService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.model}")
    private String modelName;
    @Value("${spring.ai.vertex.ai.gemini.chat.options.temperature}")
    private Double temperature;
    @Value("${spring.ai.vertex.ai.gemini.chat.options.response-mime-type}")
    private String responseMimeType;

    public GeminiAIService(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        if (modelName == null) log.info("modelName is null");
        if (temperature == null) log.info("temperature is null!");
        if (responseMimeType == null) log.info("responseMimeType is null!");

        this.chatClient = chatClientBuilder
                .defaultOptions(VertexAiGeminiChatOptions.builder()
                        .model(modelName)
                        .temperature(temperature)
                        .responseMimeType("application/json")
                        .build())
                .build();
    }

    public OrderResponseDTO getResponseMessage(String request) throws JsonProcessingException {
        String instruction= """
                You are a local restaurant recommendation assistant.
                Users send JSON requests with a message, optional preferences, and location.
                
                Your task is to:
                
                1. Extract 3–4 key keywords from the input.
                
                2. Recommend 2 real restaurants nearby (within 30 min walking distance), matching the user's preferences (e.g. food type, excluded ingredients, spice level, gender, age).
                
                3. If the user replies with another message (only contains "message"), treat it as additional context and generate a full recommendation using both inputs.
                
                4. Output must be in JSON only, and must match the input language (e.g. Korean in → Korean out).
                
                5. Include real restaurant names, addresses, and coordinates. Each must have 3–4 descriptive keywords and 2 recommended menu items, excluding banned ingredients.
                
                ex)
                First Request format:
                {
                  "message": "양식 먹고 싶어"
                }
                End Response format:
                {
                  "requestKeyword": {
                    "keyword1": "양식",
                    "keyword2": "혼자 식사",
                    "keyword3": "국물 있는 메뉴",
                    "keyword4": "오이 제외"
                  },
                  "restaurant1": {
                    "name": "파스타바운스 동탄점",
                    "address": "경기도 화성시 동탄중앙로 220",
                    "location": { "lat": 37.2108, "lon": 127.0583 },
                    "keyword": {
                      "keyword1": "혼밥 가능",
                      "keyword2": "크림 파스타",
                      "keyword3": "조용한 분위기",
                      "keyword4": "재료 변경 가능"
                    },
                    "bestMenu": {
                      "menu1": "버섯 크림 스프 파스타 (오이 제외 요청 가능)",
                      "menu2": "치킨 스튜 & 바게트"
                    }
                  },
                  "restaurant2": {
                    "name": "수프리모",
                    "address": "경기도 화성시 동탄반석로 100",
                    "location": { "lat": 37.2089, "lon": 127.0601 },
                    "keyword": {
                      "keyword1": "국물 요리",
                      "keyword2": "이탈리안 가정식",
                      "keyword3": "혼자 방문",
                      "keyword4": "20분 이내 거리"
                    },
                    "bestMenu": {
                      "menu1": "미네스트로네 수프",
                      "menu2": "감자 뇨끼 토마토 스튜"
                    }
                  }
                }
                """;

        ChatResponse chatResponse = chatClient.prompt()
                .system(instruction)
                .user(request)
                .call()
                .chatResponse();

        log.info(chatResponse.toString());

        String responseToString = chatResponse.getResult().getOutput().getText();

        return objectMapper.readValue(responseToString, OrderResponseDTO.class);
    }

}
