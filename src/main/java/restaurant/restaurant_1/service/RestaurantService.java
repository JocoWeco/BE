//Service 추가 완료
package restaurant.restaurant_1.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.stereotype.Service;
import restaurant.restaurant_1.dto.RestaurantRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class RestaurantService {
    private final Firestore firestore;
    public RestaurantService(Firestore firestore) {
        this.firestore = firestore;
    }

    public List<Map<String, Object>> searchRestaurants(RestaurantRequest request) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("restaurants")
                .whereIn("category", request.getCategory()).get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Map<String, Object>> filteredRestaurants = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            Map<String, Object> data = doc.getData();
            double dist = haversine(
                    request.getLatitude(),
                    request.getLongitude(),
                    (double) data.get("latitude"),
                    (double) data.get("longitude")
            );

            if (dist <= request.getRadius() && !containsAny(request.getAvoidIngredients(), (List<String>) data.get("main_menu"))){
                data.put("distance", (int)dist);
                filteredRestaurants.add(data);
            }
        }
        return filteredRestaurants;
    }
    //haversine 공식
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    //containsAny 함수
    private boolean containsAny(List<String> avoid, List<String> target) {
        for (String a : avoid) {
            if (target.contains(a)) return true;
        }
        return false;
    }
}
