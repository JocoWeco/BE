package com.jocoweco.foodsommelier

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    private var backPressedTime: Long = 0
    private val BACK_PRESS_INTERVAL = 2000L // 2초

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 현재 액티비티가 루트 액티비티일 때만 종료 제어
                if (isTaskRoot) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - backPressedTime < BACK_PRESS_INTERVAL) {
                        finishAffinity() // 앱 종료
                    } else {
                        Toast.makeText(
                            this@BaseActivity,
                            "뒤로 버튼을 한 번 더 누르면 종료됩니다",
                            Toast.LENGTH_SHORT
                        ).show()
                        backPressedTime = currentTime
                    }
                } else {
                    // 기본 동작: 이전 화면으로 이동
                    finish()
                }
            }
        })
    }
}