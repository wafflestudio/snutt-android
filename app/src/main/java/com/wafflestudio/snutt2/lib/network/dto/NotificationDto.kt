package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationDto(
    @param:Json(name = "_id") val id: String?,
    @param:Json(name = "title") val title: String = "",
    @param:Json(name = "message") val message: String,
    @param:Json(name = "created_at") val createdAt: String,
    @param:Json(name = "type") val type: Int,
    @param:Json(name = "deeplink") val deeplink: String?,
)
