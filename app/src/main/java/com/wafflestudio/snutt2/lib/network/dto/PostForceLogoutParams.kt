package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostForceLogoutParams(
    @Json(name = "user_id") val userId: String,
    @Json(name = "registration_id") val registrationId: String,
)
