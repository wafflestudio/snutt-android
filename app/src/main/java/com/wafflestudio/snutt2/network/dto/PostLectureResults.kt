package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


// Refactor FIXME: lectureList
@JsonClass(generateAdapter = true)
data class PostLectureResults(
    @Json(name = "year") val year: Long,
    @Json(name = "semester") val semester: Long,
    @Json(name = "title") val title: String,
    @Json(name = "_id") val id: String,
    @Json(name = "lecture_list") val lectureList: List<Any>,
    @Json(name = "updated_at") val updated_at: String
)
