package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostVerifyPwResetCodeParamsT(
    @Json(name = "user_id") val id: String,
    @Json(name = "code") val code: String,
)
