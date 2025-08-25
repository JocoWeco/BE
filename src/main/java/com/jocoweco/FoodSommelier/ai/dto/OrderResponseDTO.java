package com.jocoweco.FoodSommelier.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDTO {

    private Keyword requestKeyword;
    private Restaurant restaurant1;
    private Restaurant restaurant2;

    @Getter
    @Setter
    public static class Keyword {
        private String keyword1;
        private String keyword2;
        private String keyword3;
        private String keyword4;
    }

    @Getter
    @Setter
    public static class Restaurant {
        private String name;
        private String address;
        @JsonProperty("location")
        private Location location;
        @JsonProperty("keyword")
        private Keyword restaurantKeyword;
        @JsonProperty("bestMenu")
        private Menu menu;
    }

    @Getter
    @Setter
    public static class Location {
        @JsonProperty("lat")
        private Double latitude;
        @JsonProperty("lon")
        private Double longitude;
    }

    @Getter
    @Setter
    public static class Menu {
        private String menu1;
        private String menu2;
    }
}

