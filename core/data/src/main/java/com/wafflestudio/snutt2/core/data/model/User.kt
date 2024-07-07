package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.Nickname
import com.wafflestudio.snutt2.core.model.data.User
import com.wafflestudio.snutt2.core.network.model.UserDto

fun UserDto.toExternalModel() = User(
    isAdmin = this.isAdmin,
    email = this.email,
    localId = this.localId,
    facebookName = this.fbName,
    nickname = this.nickname?.toExternalModel() ?: Nickname("", ""),
)