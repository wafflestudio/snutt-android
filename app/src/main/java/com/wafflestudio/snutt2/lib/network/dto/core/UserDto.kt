package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.User
import com.wafflestudio.snutt2.domainmodel.Nickname

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

fun UserDto.toDomainModel(): User = User(
    email = email,
    localId = localId,
    nickname = nickname?.let { Nickname(nickname = it.nickname, tag = it.tag) },
)
