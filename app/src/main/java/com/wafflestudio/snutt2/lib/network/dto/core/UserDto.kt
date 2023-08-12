package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "isAdmin") val isAdmin: Boolean = false,
    @Json(name = "regDate") val regDate: String? = null,
    @Json(name = "notificationCheckedAt") val notificationCheckedAt: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "local_id") val localId: String? = null,
    @Json(name = "fb_name") val fbName: String? = null,
)
