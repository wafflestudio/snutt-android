package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostSocialLoginResults(
    @param:Json(name = "user_id") val userId: String,
    @param:Json(name = "token") val token: String,
    @param:Json(name = "message") val message: String,
)
