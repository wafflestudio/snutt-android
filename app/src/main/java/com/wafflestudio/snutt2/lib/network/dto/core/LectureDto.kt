package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LectureDto(
    @Json(name = "_id") val id: String,
    @Json(name = "classification") val classification: String?,
    @Json(name = "department") val department: String?,
    @Json(name = "academic_year") val academic_year: String?,
    @Json(name = "course_number") val course_number: String?,
    @Json(name = "lecture_number") val lecture_number: String?,
    @Json(name = "course_title") val course_title: String,
    @Json(name = "credit") val credit: Long,
    @Json(name = "class_time_mask") val class_time_mask: List<Long>,
    @Json(name = "class_time_json") val class_time_json: List<ClassTimeDto>,
    @Json(name = "instructor") val instructor: String,
    @Json(name = "remark") val remark: String,
    @Json(name = "category") val category: String?,
    @Json(name = "colorIndex") val colorIndex: Long = 0, // 색상
    @Json(name = "color") val color: ColorDto = ColorDto()
) {

    val isCustom: Boolean
        get() = course_number.isNullOrBlank() && lecture_number.isNullOrEmpty()
}
