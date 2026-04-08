package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// 신규
@JsonClass(generateAdapter = true)
data class FriendDto(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "userId") val userId: String,
    @param:Json(name = "displayName") val displayName: String?,
    @param:Json(name = "nickname") val nickname: NicknameDto,
    @param:Json(name = "createdAt") val createdAt: String,
)
