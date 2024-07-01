package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostSendPwResetCodeParamsT(
    @Json(name = "user_email") val email: String,
)
