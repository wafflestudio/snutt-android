package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NicknameDto(
    @Json(name = "nickname") val nickname: String = "",
    @Json(name = "tag") val tag: String = "",
)
