package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostForceLogoutParamsT(
    @Json(name = "user_id") val userId: String,
    @Json(name = "registration_id") val registrationId: String,
)
