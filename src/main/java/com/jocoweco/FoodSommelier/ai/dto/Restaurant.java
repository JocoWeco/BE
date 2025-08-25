package com.jocoweco.FoodSommelier.ai.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Restaurant {
    private String name;
    private String address;
    private Location location;
    private Keyword restaurantKeyword;
    private Menu menu;
}
