package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostLoginFacebookResults(
    @Json(name = "token") val token: String,
    @Json(name = "user_id") val userId: String,
)
