package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NicknameDtoT(
    @Json(name = "nickname") val nickname: String = "",
    @Json(name = "tag") val tag: String = "",
)
