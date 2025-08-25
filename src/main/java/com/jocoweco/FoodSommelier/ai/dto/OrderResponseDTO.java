package com.jocoweco.FoodSommelier.ai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDTO {
    private Keyword requestKeyword;
    private Restaurant restaurant1;
    private Restaurant restaurant2;
}

@Setter
@Getter
class Keyword {
    private String keyword1;
    private String keyword2;
    private String keyword3;
    private String keyword4;
}

@Setter
@Getter
class Restaurant {
    private String name;
    private String address;
    private Location location;
    private Keyword restaurantKeyword;
    private Menu menu;
}

@Setter
@Getter
class Location {
    private Double latitude;
    private Double longitude;
}

@Setter
@Getter
class Menu {
    private String menu1;
    private String menu2;
}
