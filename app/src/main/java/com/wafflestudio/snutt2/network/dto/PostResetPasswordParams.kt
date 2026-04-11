package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostResetPasswordParams(
    @param:Json(name = "user_id") val id: String,
    @param:Json(name = "password") val password: String,
    @param:Json(name = "code") val code: String,
)
