package com.jocoweco.foodsommelier.data.feat.restaurant

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("message")
    val message: String
)