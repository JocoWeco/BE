package com.jocoweco.FoodSommelier.ai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDTO {
    private String massage;
    private Double lat;
    private Double lng;
}
