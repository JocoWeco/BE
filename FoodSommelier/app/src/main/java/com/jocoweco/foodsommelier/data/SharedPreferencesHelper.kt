package com.jocoweco.foodsommelier.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SharedPreferencesHelper {
    private const val PREF_NAME = "food_sm_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // 토큰 저장
    fun saveAuthToken(context: Context, uuid: String, accessToken: String, refreshToken: String) {
        getSharedPreferences(context).edit {
            putString("UUID", uuid)
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
        }
    }

    // 토큰 불러오기
    fun getAccessToken(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_REFRESH_TOKEN, null)
    }

    fun getUUID(context: Context):String?{
        return getSharedPreferences(context).getString("UUID",null)
    }

    // 토큰 삭제 (로그아웃 시 사용)
    fun clearToken(context: Context) {
        getSharedPreferences(context).edit {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
        }
    }
}