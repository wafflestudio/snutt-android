package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostSignUpResults(
    @param:Json(name = "message") val message: String,
    @param:Json(name = "token") val token: String,
    @param:Json(name = "user_id") val userId: String,
)
