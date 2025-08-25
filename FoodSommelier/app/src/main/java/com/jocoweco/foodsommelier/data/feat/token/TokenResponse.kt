package com.jocoweco.foodsommelier.data.feat.token

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("uuid")
    val uuid: String,

    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String
)