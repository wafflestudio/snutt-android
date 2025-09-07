package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @param:Json(name = "isAdmin") val isAdmin: Boolean = false,
    @param:Json(name = "regDate") val regDate: String? = null,
    @param:Json(name = "notificationCheckedAt") val notificationCheckedAt: String? = null,
    @param:Json(name = "email") val email: String? = null,
    @param:Json(name = "localId") val localId: String? = null,
    @param:Json(name = "fbName") val fbName: String? = null,
    @param:Json(name = "nickname") val nickname: NicknameDto? = null,
)
