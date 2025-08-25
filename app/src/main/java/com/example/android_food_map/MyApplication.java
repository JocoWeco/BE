
package com.example.android_food_map;

import android.app.Application;

import com.kakao.vectormap.KakaoMapSdk;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // ✅ Kakao 지도 SDK 초기화
        KakaoMapSdk.init(this, "93598537054b335b9b76df754606d1c8");

        // ✅ Firebase 초기화 (필요할 경우)
        FirebaseApp.initializeApp(this);
    }
}
