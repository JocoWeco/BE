package com.jocoweco.foodsommelier.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.jocoweco.foodsommelier.R
import com.jocoweco.foodsommelier.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    val TAG = "Login Activity"
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    val webClientId = R.string.default_web_client_id
    private lateinit var googleSignInManager: GoogleSignInManager

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        googleSignInManager = GoogleSignInManager(this)


        /* 구글 로그인 */
        binding.btnLoginGoogle.setOnClickListener {
            googleSignInManager.signIn()
        }

        /* 로컬 로그인 */
        binding.btnLoginLocal.setOnClickListener {
            val intent = Intent(this, LoginLocalActivity::class.java)
            startActivity(intent)
        }

        /* 로컬 회원가입 */
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterLocalActivity::class.java)
            startActivity(intent)
        }
    }

    // 로그인 상태 확인
//    override fun onStart() {
//        super.onStart()
//
//
//        val currentUser = auth.currentUser
//        updateUI(currentUser)
//    }

}