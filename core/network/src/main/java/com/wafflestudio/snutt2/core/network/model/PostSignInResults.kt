package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostSignInResults(
    @Json(name = "token") val token: String,
    @Json(name = "user_id") val userId: String,
)
