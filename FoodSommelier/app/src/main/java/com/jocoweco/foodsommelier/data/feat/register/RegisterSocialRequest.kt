package com.jocoweco.foodsommelier.data.feat.register

import com.google.gson.annotations.SerializedName
import com.jocoweco.foodsommelier.data.constant.Gender

data class RegisterSocialRequest(
    @SerializedName("providerId")
    val providerId: String,

    @SerializedName("name")
    val name: String,


    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("gender")
    val gender: Gender?,

    @SerializedName("birth")
    val birth: String,
)