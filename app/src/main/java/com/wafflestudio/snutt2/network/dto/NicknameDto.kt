package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NicknameDto(
    @param:Json(name = "nickname") val nickname: String = "",
    @param:Json(name = "tag") val tag: String = "",
) {
    override fun toString(): String = "$nickname#$tag"
}
