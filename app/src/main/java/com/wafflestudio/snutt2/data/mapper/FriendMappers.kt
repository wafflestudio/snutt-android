package com.wafflestudio.snutt2.data.mapper

import com.wafflestudio.snutt2.domain.model.Friend
import com.wafflestudio.snutt2.domain.model.Nickname
import com.wafflestudio.snutt2.network.dto.FriendDto

fun FriendDto.toDomain(): Friend = Friend(
    id = id,
    userId = userId,
    displayName = displayName,
    nickname = Nickname(
        nickname = nickname.nickname,
        tag = nickname.tag,
    ),
    createdAt = createdAt,
)
