package com.jocoweco.foodsommelier.data.feat.user

import android.content.Context
import android.util.Log
import com.jocoweco.foodsommelier.data.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class User {

    // 유저 정보 가져오기
    fun getUserDataFromServer(context: Context, onResult: (UserData?) -> Unit) {

        // 서버에 요청 전달
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.getApi(context).getUserInfo()
                if (response.isSuccessful) {
                    val userData = response.body()

                    withContext(Dispatchers.Main) {
                        onResult(userData)
                    }

                } else {
                    Log.e("User", "Error: ${response.code()} - ${response.message()}")
                    withContext(Dispatchers.Main) {
                        onResult(null)
                    }
                }
            } catch (e: Exception) {
                Log.e("User", "Exception: ${e.localizedMessage}")
                withContext(Dispatchers.Main) {
                    onResult(null)
                }
            }
        }
    }
}