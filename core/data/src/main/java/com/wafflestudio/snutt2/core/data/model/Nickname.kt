package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.Nickname
import com.wafflestudio.snutt2.core.network.model.NicknameDto

fun NicknameDto.toExternalModel() = Nickname(
    nickname = nickname,
    tag = tag,
)