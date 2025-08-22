package com.example.android_food_map;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.tasks.CancellationTokenSource;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.LocationRequest;
import com.example.android_food_map.databinding.ActivityMainBinding;


//권한 확인 > 위치 설정 확인 > 위치 가져오기 순
//좌표 얻으면 MapActivity로 전달

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private ActivityMainBinding binding;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        checkLocationPermission();
    }
    /// //////////////////////////////////////////////////////////////////////////////////////////////////////////

    //위치 권한이 있는지 확인
    private void checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else{
            //권한 있으면 현재 위치 가져옴
            CancellationTokenSource cts = new CancellationTokenSource();
            fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY, cts.getToken()
            ).addOnSuccessListener(location -> {
                if (location != null){
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.d("LOCATION", "현재 위치: " + latitude + ", " + longitude);

                    //※중요※ 위치 좌표 MapActivity에 전달
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    startActivity(intent);

                } else {
                    Log.e("LOCATION", "위치를 가져올 수 없음");
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkLocationSettings();
            } else{
                Toast.makeText(this, "앱을 사용하려면 위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
/// //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkLocationSettings(){
        LocationRequest locationRequest = LocationRequest.create().setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        //위치 설정 켜져있으면 위치 가져오기
        task.addOnSuccessListener(locationSettingsResponse -> {
            CancellationTokenSource cts = new CancellationTokenSource();

            FusedLocationProviderClient locationClient =
                    LocationServices.getFusedLocationProviderClient(MainActivity.this);



            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없으면 아무 작업도 하지 않음
                return;
            }

            locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken()).addOnSuccessListener(location -> {
                if (location != null){
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.d("LOCATION", "현재 위치: " + latitude + ", " + longitude);
                }else{
                    Log.e("LOCATION", "위치를 가져올 수 없음");
                }
            });
        });

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException){
                try {
                    ((ResolvableApiException) e).startResolutionForResult(MainActivity.this, 2001);
                } catch (IntentSender.SendIntentException sendEx){
                    sendEx.printStackTrace();
                }
            }
        });
    }
}