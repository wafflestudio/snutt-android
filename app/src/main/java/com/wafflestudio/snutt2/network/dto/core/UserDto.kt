package com.wafflestudio.snutt2.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "isAdmin") val isAdmin: Boolean,
    @Json(name = "regDate") val regDate: String,
    @Json(name = "notificationCheckedAt") val notificationCheckedAt: String,
    @Json(name = "email") val email: String,
    @Json(name = "local_id") val localId: String,
    @Json(name = "fb_name") val fbName: String
)
