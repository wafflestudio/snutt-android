package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiaryTargetLectureDto(
    @param:Json(name = "lectureId") val lectureId: String,
    @param:Json(name = "courseTitle") val courseTitle: String,
)
