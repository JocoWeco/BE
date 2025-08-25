package com.jocoweco.foodsommelier.data.feat.login

import com.google.gson.annotations.SerializedName

data class CheckNicknameRequest(
    @SerializedName("nickname")
    val nickname: String
)