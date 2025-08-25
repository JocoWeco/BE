package com.jocoweco.foodsommelier.login

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.jocoweco.foodsommelier.BaseActivity
import com.jocoweco.foodsommelier.MainActivity
import com.jocoweco.foodsommelier.databinding.ActivityLoginLocalBinding
import com.jocoweco.foodsommelier.data.feat.login.LoginLocalRequest
import com.jocoweco.foodsommelier.data.RetrofitInstance
import com.jocoweco.foodsommelier.data.SharedPreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response

class LoginLocalActivity : BaseActivity() {
    val TAG = "Login Local Activity"

    private lateinit var etLoginId: EditText
    private lateinit var etPassword: EditText

    private val binding by lazy { ActivityLoginLocalBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        etLoginId = binding.etLocalId
        etPassword = binding.etLocalPw

        binding.btnLogin.setOnClickListener {
            sendDataToServer()
        }

    }

    private fun sendDataToServer() {
        val request = LoginLocalRequest(
            loginId = etLoginId.text.toString(),
            password = etPassword.text.toString()
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val result = runSafeApiCall(
                onError = { msg -> showToast(msg) }) {
                RetrofitInstance.getApi(this@LoginLocalActivity).login(request)
            }
            withContext(Dispatchers.Main) {
                if (result?.isSuccessful == true) {
                    val loginResponse = result.body()
                    if (loginResponse != null) {

                        // 앱에 토큰 저장
                        saveTokens(
                            loginResponse.uuid,
                            loginResponse.accessToken,
                            loginResponse.refreshToken
                        )

                        val intent = Intent(this@LoginLocalActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        showToast("로그인 성공")
                    } else {
                        showToast("서버 응답이 올바르지 않습니다.")
                    }

                } else {
                    showToast("로그인 실패")
                }
            }
        }

    }

    private fun saveTokens(uuid: String, accessToken: String, refreshToken: String) {
        SharedPreferencesHelper.saveAuthToken(this, uuid, accessToken, refreshToken)
    }

    private fun showToast(message: String) {
        Toast.makeText(this@LoginLocalActivity, message, Toast.LENGTH_SHORT).show()
    }

    suspend fun <T> runSafeApiCall(
        onError: (String) -> Unit = {},
        block: suspend () -> Response<T>
    ): Response<T>? {
        return try {
            block()
        } catch (e: IOException) {
            val msg = "네트워크 오류 발생: ${e.message}"
            withContext(Dispatchers.Main) { onError(msg) }
            onError(msg)
            null
        } catch (e: HttpException) {
            val msg = "HTTP 오류 발생: ${e.code()} ${e.message}"
            withContext(Dispatchers.Main) { onError(msg) }
            onError(msg)
            null
        } catch (e: Exception) {
            val msg = "기타 오류 발생: ${e.message}"
            Log.e("Error", msg)
            withContext(Dispatchers.Main) { onError(msg) }
            null
        }
    }
}