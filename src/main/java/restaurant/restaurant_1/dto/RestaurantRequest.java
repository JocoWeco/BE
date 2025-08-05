package restaurant.restaurant_1.dto;

import java.util.List;
import lombok.Data;

@Data
public class RestaurantRequest {
    private String query;
    private List<String> category;

    private int tasteSpicy;
    private String tasteSalty;
    private List<String> texture;
    private List<String> atmosphere;
    private List<String> ingredients;
    private List<String> avoidIngredients;

    private double latitude;
    private double longitude;
    private int radius;
}

