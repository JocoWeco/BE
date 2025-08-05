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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController{

    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> searchRestaurants(@RequestBody RestaurantRequest request) throws Exception{
        Firestore db = FirestoreClient.getFirestore(); //firebase에서 데이터 가져오기
        ApiFuture<QuerySnapshot> future = db.collection("restaurants").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Map<String, Object>> filterd = new ArrayList<>();

        for (QueryDocumentSnapshot doc : documents) {
            Map<String, Object> data = doc.getData();

            //거리 계산
            //아래 haversine 공식 참고
            double dist = haversine(request.getLatitude(), request.getLongitude(),
                    (double)data.get("latitude"), (double)data.get("longitude") );

            //조건 확인
            if (dist <= request.getRadius() && request.getCategory().contains(data.get("category")) //식당의 카테고리랑 사용자가 원하는 카테고리가 맞을 때
                    && !containsAny(request.getAvoidIngredients(), (List<String>) data.get("main_menu"))
            ) {
                data.put("distance", (int)dist); //식당에 거리 정보 추가
                filterd.add(data);
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("restaurants", filterd);
        return ResponseEntity.ok(response);

    }
    /// /////////////////////////////////////////////////////////////////////////////////////////////////
    //haversine 공식
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // 지구 반지름 (단위: m)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 반환값: 두 지점 간 거리 (단위: 미터)
    }

    //containsAny 함수
    private boolean containsAny(List<String> avoid, List<String> target) {
        for (String a : avoid) {
            if (target.contains(a)) return true;
        }
        return false;
    }
    /// /////////////////////////////////////////////////////////////////////////////////////////////////


}

