package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostLectureParams(
    @param:Json(name = "is_forced") var id: Boolean,
)
