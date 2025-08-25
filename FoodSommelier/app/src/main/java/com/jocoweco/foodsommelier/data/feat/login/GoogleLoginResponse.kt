package com.jocoweco.foodsommelier.data.feat.login

import com.google.gson.annotations.SerializedName

data class GoogleLoginResponse (
    @SerializedName("uuid")
    val uuid:String,

    @SerializedName("isNewUser")
    val isNewUser:Boolean,

    @SerializedName("accessToken")
    val accessToken:String,

    @SerializedName("refreshToken")
    val refreshToken:String
)