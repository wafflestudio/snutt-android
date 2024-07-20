package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LectureDto(
    @Json(name = "_id") val id: String,
    @Json(name = "lecture_id") val lecture_id: String? = null,
    @Json(name = "classification") val classification: String?,
    @Json(name = "department") val department: String?,
    @Json(name = "academic_year") val academic_year: String?,
    @Json(name = "course_number") val course_number: String?,
    @Json(name = "lecture_number") val lecture_number: String?,
    @Json(name = "course_title") val course_title: String,
    @Json(name = "credit") val credit: Long,
    @Json(name = "class_time_json") val class_time_json: List<ClassTimeDto>,
    @Json(name = "instructor") val instructor: String,
    @Json(name = "quota") val quota: Long = 0,
    @Json(name = "freshmanQuota") val freshmanQuota: Long?,
    @Json(name = "remark") val remark: String,
    @Json(name = "category") val category: String?,
    @Json(name = "colorIndex") val colorIndex: Long = 0, // 색상
    @Json(name = "color") val color: ColorDto = ColorDto(),
    @Json(name = "registrationCount") val registrationCount: Long = 0,
    @Json(name = "wasFull") val wasFull: Boolean = false,
)