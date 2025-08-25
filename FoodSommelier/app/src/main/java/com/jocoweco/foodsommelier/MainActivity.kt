package com.jocoweco.foodsommelier

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jocoweco.foodsommelier.databinding.ActivityMainBinding
import com.jocoweco.foodsommelier.fragment.FavoriteFragment
import com.jocoweco.foodsommelier.fragment.OrderOptionFragment
import com.jocoweco.foodsommelier.fragment.OrderTextFragment
import com.kakao.vectormap.GestureType
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import kotlin.system.exitProcess

class MainActivity : BaseActivity() {
    private lateinit var btnGoCenter: ImageButton
    private lateinit var mapView: MapView
    private var currentLocation: LatLng? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var requestLocationUpdates: Boolean = false
    private lateinit var centerLabel: Label
    private var isMapStarted = false
    private var isTracking = true

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineGranted || coarseGranted) {
                onLocationPermissionGranted()
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                showPermissionDeniedDialog()
            }
        }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mapView=binding.mapView
        btnGoCenter = binding.btnGoCenter

        val bottomSheetLayout = binding.bottomSheetLayout
        val behavior = BottomSheetBehavior.from(bottomSheetLayout)

        // 초기 화면 설정
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // 검색바와 버튼이 겹치지 않도록 padding 처리
            binding.searchBar.updatePadding(top = systemBars.top)
            binding.btnGoCenter.updatePadding(bottom = systemBars.bottom)
            bottomSheetLayout.updatePadding(bottom = systemBars.bottom)

            insets
        }
        ViewCompat.requestApplyInsets(binding.root)

        replaceBottomSheetFragment(FavoriteFragment())


        // 화면의 80%만 채우기
        val displayMetrics = Resources.getSystem().displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val maxHeight = (screenHeight * 0.8).toInt()
        bottomSheetLayout.layoutParams.height = maxHeight
        bottomSheetLayout.requestLayout()

        // 카카오 SDK 초기화
        KakaoMapSdk.init(this, getString(R.string.kakao_native_app_key))

        // 객체 생성
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L).build()


        // 위치 권한 확인
        checkLocationPermission()


        binding.btnFavorite.setOnClickListener {
            replaceBottomSheetFragment(FavoriteFragment())
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.btnOrderText.setOnClickListener {
            replaceBottomSheetFragment(OrderTextFragment())
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.btnOrderSimple.setOnClickListener {
            replaceBottomSheetFragment(OrderOptionFragment())
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.btnMy.setOnClickListener {
            replaceBottomSheetFragment(MyPageFragment())
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }


        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.btnGoCenter.visibility =
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    private fun replaceBottomSheetFragment(fragment: Fragment) {
        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.bottom_sheet_fragment_container)
        if (currentFragment?.javaClass == fragment.javaClass) return

        supportFragmentManager.beginTransaction()
            .replace(R.id.bottom_sheet_fragment_container, fragment)
            .commit()
    }

    // 지도 표시
    private fun showKakaoMap() {
        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                    // 지도 API 정상적으로 종료
                    Log.d("Kakao Map", "onMapDestroy")
                }

                override fun onMapError(error: Exception?) {
                    error?.printStackTrace()
                    Log.e("Kakao Map", "onMapError", error)
                }

            }, object : KakaoMapReadyCallback() {
                @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
                override fun onMapReady(kakaoMap: KakaoMap) {
                    // 카카오 지도 API 정상적으로 시작
                    Log.i("Kakao Map", "Kakao Map Start")
                    isMapStarted = true

                    // 라벨 스타일
                    val styleCurrentLocation = LabelStyle.from(R.drawable.ic_label)
                        .setAnchorPoint(0.5f, 1.0f)

                    val styles = LabelStyles.from(styleCurrentLocation)
                    kakaoMap.labelManager?.addLabelStyles(styles)

                    // 라벨 생성
                    val layer = kakaoMap.labelManager?.layer

                    centerLabel = layer?.addLabel(
                        LabelOptions.from(
                            "centerLabel",
                            currentLocation ?: LatLng.from(36.9073, 127.1430)
                        )
                            .setStyles(styles)
                            .setRank(1)
                    )!!

                    locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            for (location in locationResult.locations) {
                                currentLocation = LatLng.from(location.latitude, location.longitude)
                                centerLabel.moveTo(currentLocation)
                            }
                            if (isTracking) {
                                val cameraUpdate =
                                    CameraUpdateFactory.newCenterPosition(currentLocation)
                                kakaoMap.moveCamera(cameraUpdate)
                                kakaoMap.trackingManager?.startTracking(centerLabel)
                            }
                        }
                    }

                    // 위치 업데이트
                    updateCurrentLocation()

                    // 지도 중심으로 이동
                    val cameraUpdate = CameraUpdateFactory.newCenterPosition(currentLocation)
                    kakaoMap.moveCamera(cameraUpdate)

                    val trackingManager = kakaoMap.trackingManager
                    trackingManager?.setTrackingRotation(true)
                    trackingManager?.startTracking(centerLabel)
                    isTracking = true

                    // 지도 이동 감지하면 추적 중지
                    kakaoMap.setOnCameraMoveEndListener { _, _, gesture ->
                        if (isTracking && gesture != GestureType.Unknown) {
                            kakaoMap.trackingManager?.stopTracking()
                            isTracking = false
                            Log.d("Kakao Map", "Tracking stopped by user movement")
                        }
                    }

                    // 버튼 클릭 시 화면 중앙으로 라벨 위치 고정
                    binding.btnGoCenter.setOnClickListener {
                        if (currentLocation != null) {
                            val cameraUpdate =
                                CameraUpdateFactory.newCenterPosition(currentLocation)

                            kakaoMap.moveCamera(cameraUpdate)
                            trackingManager?.startTracking(centerLabel)

                            isTracking = true
                            Log.d("Kakao Map", "Tracking started")
                        }
                    }
                }

                override fun getPosition(): LatLng {

                    return currentLocation ?: LatLng.from(36.9073, 127.1430)
                }

                override fun getZoomLevel(): Int {
                    return 17
                }

            })
    }

    override fun onResume() {
        super.onResume()
        if (isMapStarted) {
            try {
                mapView.resume()
            } catch (e: Exception) {
                Log.e("Kakao Map", "resume() 오류", e)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        if (isMapStarted) {
            try {
                mapView.pause()
            } catch (e: Exception) {
                Log.e("Kakao Map", "pause() 오류", e)
            }
        }
    }

    // 위치 권한 확인
    private fun checkLocationPermission() {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
        ) {
            // 위치 권한 허용
            onLocationPermissionGranted()
        } else {
            // 위치 권한 X
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            showPermissionDeniedDialog()
        }
    }

    // 위치 접근 허용
    private fun onLocationPermissionGranted() {
        Log.i("Permission", "위치 권한 허용됨")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        showKakaoMap()
    }

    // 권한 허용 다이얼로그
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setMessage("위치 권한 거부시 앱을 사용할 수 없습니다.")
            .setPositiveButton("권한 설정하러 가기") { dialog: DialogInterface, which: Int ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_SETTINGS).apply {
                        data = "package:$packageName".toUri()
                    }
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                    startActivity(intent)
                } finally {
                    finish()
                }
            }.setNegativeButton("앱 종료하기") { dialog, which ->
                exitProcess(0)
            }
            .setCancelable(false)
            .show()
    }

    // 현재 위치 가져오기
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun updateCurrentLocation() {
        requestLocationUpdates = true
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

}