package com.jocoweco.foodsommelier.data.feat.token

import android.content.Context
import android.util.Log
import com.jocoweco.foodsommelier.data.RetrofitInstance
import com.jocoweco.foodsommelier.data.SharedPreferencesHelper
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.HttpException
import java.io.IOException

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        // SharedPref에서 토큰 가져오기
        val accessToken = SharedPreferencesHelper.getAccessToken(context)
        if (accessToken.isNullOrEmpty()) {
            return chain.proceed(chain.request())
        }

        // 요청에 Authorization 헤더 추가
        var request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        var response = chain.proceed(request)

        // 401 Unauthorized 발생 시 새 토큰 발급
        if (response.code == 401) {
            response.close() // 이전 요청 닫기

            val refreshToken = SharedPreferencesHelper.getRefreshToken(context)
            if (!refreshToken.isNullOrEmpty()) {
                val refreshResponse = runBlocking {
                    try {
                        RetrofitInstance.getApi(context).reissue(RefreshTokenRequest(refreshToken))
                    } catch (e: IOException) {
                        // 네트워크 오류
                        Log.e("AuthInterceptor", "Network error during refresh token request", e)
                        null
                    } catch (e: HttpException) {
                        // HTTP 응답 오류 (4xx, 5xx)
                        Log.e(
                            "AuthInterceptor",
                            "HTTP error during refresh token request: ${e.code()}",
                            e
                        )
                        null
                    } catch (e: Exception) {
                        // 기타 예외
                        Log.e("AuthInterceptor", "Unexpected error during refresh token request", e)
                        null
                    }
                }
                if (refreshResponse != null && refreshResponse.isSuccessful) {
                    val tokenBody: TokenResponse? = refreshResponse.body()

                    if (tokenBody != null) {
                        // SharedPref에 새 토큰 저장
                        SharedPreferencesHelper.saveAuthToken(
                            uuid = tokenBody.uuid,
                            accessToken = tokenBody.accessToken,
                            refreshToken = tokenBody.refreshToken,
                            context = context
                        )

                        // 원래 요청 새 액세스 토큰으로 재요청
                        request = chain.request().newBuilder()
                            .removeHeader("Authorization")
                            .addHeader("Authorization", "Bearer ${tokenBody.accessToken}")
                            .build()

                        response = chain.proceed(request)
                    }
                }
            }
        }
        return response

    }
}