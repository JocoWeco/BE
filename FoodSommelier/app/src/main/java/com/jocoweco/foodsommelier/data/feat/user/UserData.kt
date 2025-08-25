package com.jocoweco.foodsommelier.data.feat.user

import com.google.gson.annotations.SerializedName
import com.jocoweco.foodsommelier.data.constant.Gender

// 사용자 정보를 담을 클래스
data class UserData(
    @SerializedName("uuid")
    val uuid: String,

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("birth")
    val birth: String,

    @SerializedName("gender")
    val gender: Gender,

    @SerializedName("excludedIngredient")
    val excludedIngredient: String
)

