package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domain.model.User

@JsonClass(generateAdapter = true)
data class UserLocalEntity(
    val isAdmin: Boolean = false,
    val regDate: String? = null,
    val notificationCheckedAt: String? = null,
    val email: String? = null,
    val localId: String? = null,
    val fbName: String? = null,
    val nickname: NicknameLocalEntity? = null,
)

fun UserLocalEntity.toDomainModel(): User = User(
    email = email,
    localId = localId,
    nickname = nickname?.toDomainModel(),
)
