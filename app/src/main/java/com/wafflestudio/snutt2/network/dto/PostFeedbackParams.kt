package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostFeedbackParams(
    @param:Json(name = "email") val email: String,
    @param:Json(name = "message") val message: String,
)
