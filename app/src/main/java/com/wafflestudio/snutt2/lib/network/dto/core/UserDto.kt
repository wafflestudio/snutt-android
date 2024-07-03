package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.UserDto as UserDtoNetwork
import com.wafflestudio.snutt2.core.database.model.User

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "isAdmin") val isAdmin: Boolean = false,
    @Json(name = "regDate") val regDate: String? = null,
    @Json(name = "notificationCheckedAt") val notificationCheckedAt: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "localId") val localId: String? = null,
    @Json(name = "fbName") val fbName: String? = null,
    @Json(name = "nickname") val nickname: NicknameDto? = null,
)

fun UserDtoNetwork.toExternalModel() = UserDto(
    isAdmin = this.isAdmin,
    regDate = this.regDate,
    notificationCheckedAt = this.notificationCheckedAt,
    email = this.email,
    localId = this.localId,
    fbName = this.fbName,
    nickname = this.nickname?.toExternalModel(),
)

fun User.toExternalModel() = UserDto(
    isAdmin = this.isAdmin,
    regDate = this.regDate,
    notificationCheckedAt = this.notificationCheckedAt,
    email = this.email,
    localId = this.localId,
    fbName = this.fbName,
    nickname = this.nickname?.toExternalModel(),
)

fun UserDto.toDatabaseModel() = User(
    isAdmin = this.isAdmin,
    regDate = this.regDate,
    notificationCheckedAt = this.notificationCheckedAt,
    email = this.email,
    localId = this.localId,
    fbName = this.fbName,
    nickname = this.nickname?.toDatabaseModel(),
)