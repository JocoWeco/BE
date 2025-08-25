package com.jocoweco.foodsommelier.data.feat.login

import com.google.gson.annotations.SerializedName

data class LoginLocalRequest(
    @SerializedName("loginId")
    val loginId: String,

    @SerializedName("password")
    val password: String
)