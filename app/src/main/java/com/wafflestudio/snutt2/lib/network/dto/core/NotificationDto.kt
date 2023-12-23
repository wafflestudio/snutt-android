package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationDto(
    @Json(name = "_id") val id: String?,
    @Json(name = "title") val title: String = "",
    @Json(name = "message") val message: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "type") val type: Int,
    @Json(name = "detail") val detail: Detail?,
) {

    @JsonClass(generateAdapter = true)
    data class Detail(
        @Json(name = "course_title") val courseTitle: String?,
        @Json(name = "lecture_number") val lectureNumber: String?,
        @Json(name = "course_number") val courseNumber: String?,
    )
}
