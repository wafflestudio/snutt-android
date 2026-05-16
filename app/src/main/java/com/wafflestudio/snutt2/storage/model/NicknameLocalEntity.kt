package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domain.model.Nickname

@JsonClass(generateAdapter = true)
data class NicknameLocalEntity(
    val nickname: String = "",
    val tag: String = "",
)

fun NicknameLocalEntity.toDomainModel(): Nickname = Nickname(
    nickname = nickname,
    tag = tag,
)
