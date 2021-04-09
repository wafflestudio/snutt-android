package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class GetTableByIdResults(
    @Json(name = "year") val year: Long,
    @Json(name = "title") val title: String,
    @Json(name = "_id") val _id: String,
    @Json(name = "lecture_list") val lectureList: List<Any>,
    @Json(name = "updated_at") val updatedAt: String
)
