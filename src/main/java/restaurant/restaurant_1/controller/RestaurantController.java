// ※Postman 에서 테스트 완료※
//Service 추가 후 수정 완
package restaurant.restaurant_1.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import restaurant.restaurant_1.dto.RestaurantRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import restaurant.restaurant_1.service.RestaurantService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController{

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> searchRestaurants(@RequestBody RestaurantRequest request) throws Exception{

        List<Map<String, Object>> filterdRestaurants = restaurantService.searchRestaurants(request);

        Map<String, Object> response = new HashMap<>();
        response.put("restaurants", filterdRestaurants);
        return ResponseEntity.ok(response);

    }
    /// /////////////////////////////////////////////////////////////////////////////////////////////////
}

