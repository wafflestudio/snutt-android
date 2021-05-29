package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostSignUpResults(
    @Json(name = "message") val message: String,
    @Json(name = "token") val token: String,
    @Json(name = "user_id") val userId: String
)
