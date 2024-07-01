package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostSignUpParamsT(
    @Json(name = "id") val id: String,
    @Json(name = "password") val password: String,
    @Json(name = "email") val email: String,
)
