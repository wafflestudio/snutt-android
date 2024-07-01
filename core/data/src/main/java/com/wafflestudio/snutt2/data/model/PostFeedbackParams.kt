package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostFeedbackParamsT(
    @Json(name = "email") val email: String,
    @Json(name = "message") val message: String,
)
