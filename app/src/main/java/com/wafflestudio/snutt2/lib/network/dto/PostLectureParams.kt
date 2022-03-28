package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Refactor FIXME empty body
@JsonClass(generateAdapter = true)
class PostLectureParams()

// 임시로 여기 추가
@JsonClass(generateAdapter = true)
data class PostLectureForce(
    @Json(name = "is_forced") var id: Boolean
)
