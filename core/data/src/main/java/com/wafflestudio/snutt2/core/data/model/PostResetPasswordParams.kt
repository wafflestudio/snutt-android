package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostResetPasswordParamsT(
    @Json(name = "user_id") val id: String,
    @Json(name = "password") val password: String,
)
