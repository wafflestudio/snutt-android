package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationDto(
    @Json(name = "_id") val id: String?,
    @Json(name = "title") val title: String = "",
    @Json(name = "message") val message: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "type") val type: Int,
    @Json(name = "deeplink") val deeplink: String?,
)
