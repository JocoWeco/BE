package com.example.android_food_map;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;

public class MapActivity extends AppCompatActivity {
    private MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map); // FrameLayout(id=map_container) 포함 레이아웃

        //MainActivity 에서 위도, 경도 받음
        double latitude = getIntent().getDoubleExtra("latitude", Double.NaN);
        double longitude = getIntent().getDoubleExtra("longitude", Double.NaN);
        if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
            Toast.makeText(this, "위치 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        try{
            mapView = new MapView(this);
            //mapView 추가하기 전 null 체크
            FrameLayout mapContainer = findViewById(R.id.map_container);
            if (mapContainer != null && mapView != null){
                mapContainer.addView(mapView);
            } else {
                //뷰를 찾지 못했거나, mapView 생성 실패할 때
                Toast.makeText(this, "지도 컨테이너 또는 지도를 초기화할 수 없습니다.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            mapView.start(new MapLifeCycleCallback() {
                // 지도 API 가 정상적으로 종료될 때 호출
                @Override public void onMapDestroy() {}
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출
                @Override public void onMapError(Exception error) {
                    error.printStackTrace();
                    Toast.makeText(MapActivity.this, "지도 초기화 중 오류가 발생했습니다.: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }, new KakaoMapReadyCallback() {
                @Override public void onMapReady(@NonNull KakaoMap kakaoMap) {
                    // 인증 후 API 가 정상적으로 실행될 때 호출됨
                    LatLng me = LatLng.from(latitude, longitude);
                    //카메라 내 위치로 이동함
                    kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(me));
                    //내 위치에 마커 추가
                    LabelStyle style = LabelStyle.from(R.drawable.ic_my_location_pin);
                    LabelStyles styles = LabelStyles.from(style);
                    //라벨 좌표, 스타일, 텍스트 설정
                    LabelOptions options = LabelOptions.from("myLocationMarker", me)
                            .setStyles(styles);
                    LabelLayer layer = kakaoMap.getLabelManager().getLayer();
                    layer.addLabel(options);
                }
            });
        } catch (Exception e) {
            //예상치 못한 예외 발생할 때
            e.printStackTrace();
            Toast.makeText(this, "지도 초기화 중 예기치 못한 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(mapView != null){
            mapView.resume();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mapView != null){
            mapView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.finish();
    }
}