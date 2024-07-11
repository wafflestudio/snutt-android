package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.Nickname
import com.wafflestudio.snutt2.core.model.data.User
import com.wafflestudio.snutt2.core.network.model.UserDto

fun UserDto.toExternalModel() = User(
    isAdmin = isAdmin,
    email = email,
    localId = localId,
    facebookName = fbName,
    nickname = nickname?.toExternalModel() ?: Nickname("", ""),
)