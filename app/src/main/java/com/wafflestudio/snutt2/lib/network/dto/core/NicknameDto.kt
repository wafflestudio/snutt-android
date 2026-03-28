package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.Nickname

@JsonClass(generateAdapter = true)
data class NicknameDto(
    @param:Json(name = "nickname") val nickname: String = "",
    @param:Json(name = "tag") val tag: String = "",
) {
    override fun toString(): String {
        return "$nickname#$tag"
    }

    fun toDomainModel(): Nickname = Nickname(nickname, tag)
}
