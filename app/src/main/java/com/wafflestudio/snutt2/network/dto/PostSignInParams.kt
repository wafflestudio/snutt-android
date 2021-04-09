package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PostSignInParams(
    @Json(name = "id") val id: String,
    @Json(name = "password") val password: String
)
