package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.database.model.Nickname
import com.wafflestudio.snutt2.core.network.model.NicknameDto as NicknameDtoNetwork

@JsonClass(generateAdapter = true)
data class NicknameDto(
    @Json(name = "nickname") val nickname: String = "",
    @Json(name = "tag") val tag: String = "",
) {
    override fun toString(): String {
        return "$nickname#$tag"
    }
}

fun NicknameDtoNetwork.toExternalModel() = NicknameDto(
    nickname = this.nickname,
    tag = this.tag,
)

fun Nickname.toExternalModel() = NicknameDto(
    nickname = this.nickname,
    tag = this.tag,
)

fun NicknameDto.toDatabaseModel() = Nickname(
    nickname = this.nickname,
    tag = this.tag,
)