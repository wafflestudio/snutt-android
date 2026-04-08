package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// 신규
@JsonClass(generateAdapter = true)
data class SnuttEvLectureIdDto(
    @param:Json(name = "snuttId") val snuttId: String? = null,
    @param:Json(name = "evLectureId") val evLectureId: Long,
)
