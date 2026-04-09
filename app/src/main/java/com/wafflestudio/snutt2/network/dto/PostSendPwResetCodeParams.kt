package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostSendPwResetCodeParams(
    @param:Json(name = "user_email") val email: String,
)
