package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostLoginFacebookResultsT(
    @Json(name = "token") val token: String,
    @Json(name = "user_id") val userId: String,
)
