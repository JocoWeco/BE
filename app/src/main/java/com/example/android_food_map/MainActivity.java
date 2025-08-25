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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.Task;
import com.example.android_food_map.databinding.ActivityMainBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int REQUEST_CHECK_SETTINGS = 2001;
    private ActivityMainBinding binding;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkLocationSettings();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationSettings();
            } else {
                Toast.makeText(this, "앱을 사용하려면 위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create().setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            // 1단계: 마지막으로 알려진 위치를 먼저 시도
            fusedLocationClient.getLastLocation().addOnSuccessListener(lastLocation -> {
                if (lastLocation != null) {
                    startMapActivity(lastLocation.getLatitude(), lastLocation.getLongitude());
                } else {
                    Log.d("LOCATION", "getLastLocation() 실패. getCurrentLocation() 시도.");
                    // 2단계: 마지막 위치가 없다면 현재 위치를 요청
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationTokenSource().getToken()).addOnSuccessListener(currentLocation -> {
                        if (currentLocation != null) {
                            Log.d("LOCATION", "getCurrentLocation() 성공");
                            startMapActivity(currentLocation.getLatitude(), currentLocation.getLongitude());
                        } else {
                            Log.e("LOCATION", "위치를 가져올 수 없음");
                            Toast.makeText(this, "위치를 가져올 수 없습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        });

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ((ResolvableApiException) e).startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    sendEx.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                checkLocationSettings();
            } else {
                Toast.makeText(this, "위치 설정을 켜야 지도를 볼 수 있습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startMapActivity(double latitude, double longitude) {
        Log.d("LOCATION", "현재 위치: " + latitude + ", " + longitude);
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }
}