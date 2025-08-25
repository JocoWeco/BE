package com.jocoweco.foodsommelier.data.feat.token

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequest(
    @SerializedName("refreshToken")
    val refreshToken: String
)