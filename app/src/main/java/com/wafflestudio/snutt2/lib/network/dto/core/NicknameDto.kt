package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NicknameDto(
    @Json(name = "nickname") val nickname: String = "",
    @Json(name = "tag") val tag: String = "",
) {
    override fun toString(): String {
        return "$nickname#$tag"
    }
}
