package com.wafflestudio.snutt2.data.mapper

import com.wafflestudio.snutt2.domainmodel.Friend
import com.wafflestudio.snutt2.domainmodel.Nickname
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
