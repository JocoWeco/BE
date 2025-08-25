package com.jocoweco.foodsommelier.data.feat.register

import com.google.gson.annotations.SerializedName
import com.jocoweco.foodsommelier.data.constant.Gender

data class RegisterLocalRequest(

    @SerializedName("loginId")
    val loginId: String,

    @SerializedName("password")
    val password: String,


    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("gender")
    val gender: Gender?,

    @SerializedName("birth")
    val birth: String,
)