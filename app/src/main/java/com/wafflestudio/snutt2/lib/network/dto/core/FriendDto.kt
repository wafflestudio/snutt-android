package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.Friend
import com.wafflestudio.snutt2.domainmodel.Nickname

// 신규
@JsonClass(generateAdapter = true)
data class FriendDto(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "userId") val userId: String,
    @param:Json(name = "displayName") val displayName: String?,
    @param:Json(name = "nickname") val nickname: NicknameDto,
    @param:Json(name = "createdAt") val createdAt: String,
) {
    fun toDomainModel(): Friend {
        return Friend(
            id = id,
            userId = userId,
            displayName = displayName,
            nickname = Nickname(
                nickname = nickname.nickname,
                tag = nickname.tag,
            ),
            createdAt = createdAt,
        )
    }
}
