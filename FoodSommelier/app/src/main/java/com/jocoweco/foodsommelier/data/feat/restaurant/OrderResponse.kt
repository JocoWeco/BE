package com.jocoweco.foodsommelier.data.feat.restaurant

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("response")
    val response: String
)