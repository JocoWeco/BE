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


        mapView = new MapView(this);
        ((FrameLayout) findViewById(R.id.map_container)).addView(mapView);

        //지도 시작, 카메라 이동, 마커 추가
        mapView.start(new MapLifeCycleCallback() {
            @Override public void onMapDestroy() {}
            @Override public void onMapError(Exception error) { error.printStackTrace(); }
        }, new KakaoMapReadyCallback() {
            @Override public void onMapReady(@NonNull KakaoMap kakaoMap) {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.finish();
    }
}