package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostVerifyPwResetCodeParams(
    @Json(name = "user_id") val id: String,
    @Json(name = "code") val code: String,
)
