package com.wafflestudio.snutt2.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class NotificationDto(
    @Json(name = "_id") val id: String,
    @Json(name = "message") val message: String,
    @Json(name = "crated_at") val crated_at: String,
    @Json(name = "detail") val detail: Detail
) {

    @JsonClass(generateAdapter = true)
    data class Detail(
        @Json(name = "course_title") val courseTitle: String,
        @Json(name = "lecture_number") val lectureNumber: String,
        @Json(name = "course_number") val courseNumber: String
    )
}
