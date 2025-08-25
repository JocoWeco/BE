package com.jocoweco.foodsommelier.data

import android.content.Context
import com.jocoweco.foodsommelier.data.feat.token.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://13.125.199.251:8080/"

    fun getRetrofit(context: Context): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context)) // 토큰 + 401 자동 처리
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getApi(context: Context): ApiService {
        return getRetrofit(context).create(ApiService::class.java)
    }

}