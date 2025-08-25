package com.jocoweco.foodsommelier.data.feat.login

import com.google.gson.annotations.SerializedName

data class GoogleLoginRequest(
    @SerializedName("googleIdToken")
    val googleIdToken: String
)