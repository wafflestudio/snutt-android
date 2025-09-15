package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostForceLogoutParams(
    @param:Json(name = "user_id") val userId: String,
    @param:Json(name = "registration_id") val registrationId: String,
)
