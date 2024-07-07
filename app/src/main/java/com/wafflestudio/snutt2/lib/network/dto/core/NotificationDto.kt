package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.NotificationDto as NotificationDtoNetwork

@JsonClass(generateAdapter = true)
data class NotificationDto(
    @Json(name = "_id") val id: String?,
    @Json(name = "title") val title: String = "",
    @Json(name = "message") val message: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "type") val type: Int,
    @Json(name = "detail") val detail: Detail?,
    @Json(name = "deeplink") val deeplink: String?,
) {

    @JsonClass(generateAdapter = true)
    data class Detail(
        @Json(name = "course_title") val courseTitle: String?,
        @Json(name = "lecture_number") val lectureNumber: String?,
        @Json(name = "course_number") val courseNumber: String?,
    )
}

fun NotificationDtoNetwork.toExternalModel() = NotificationDto(
    id = this.id,
    title = this.title,
    message = this.message,
    createdAt = this.createdAt,
    type = this.type,
    detail = this.detail?.toExternalModel(),
    deeplink = this.deeplink,
)

fun NotificationDtoNetwork.Detail.toExternalModel() = NotificationDto.Detail(
    courseTitle = this.courseTitle,
    lectureNumber = this.lectureNumber,
    courseNumber = this.courseNumber,
)