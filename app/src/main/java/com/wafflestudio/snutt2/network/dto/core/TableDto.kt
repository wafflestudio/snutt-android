package com.wafflestudio.snutt2.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TableDto(
    @Json(name = "year") val year: Long,
    @Json(name = "semester") val semester: Long,
    @Json(name = "title") val title: String,
    @Json(name = "_id") val _id: String,
    @Json(name = "lecture_list") val lectureList: List<LectureDto>,
    @Json(name = "updated_at") val updated_at: String
)
