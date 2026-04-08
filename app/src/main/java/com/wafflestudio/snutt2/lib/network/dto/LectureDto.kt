package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LectureDto(
    @param:Json(name = "_id") val id: String,
    @param:Json(name = "lecture_id") val lecture_id: String? = null,
    @param:Json(name = "classification") val classification: String?,
    @param:Json(name = "department") val department: String?,
    @param:Json(name = "academic_year") val academic_year: String?,
    @param:Json(name = "course_number") val course_number: String?,
    @param:Json(name = "lecture_number") val lecture_number: String?,
    @param:Json(name = "course_title") val course_title: String,
    @param:Json(name = "credit") val credit: Long,
    @param:Json(name = "class_time_json") val class_time_json: List<ClassTimeDto>,
    @param:Json(name = "instructor") val instructor: String,
    @param:Json(name = "quota") val quota: Long = 0,
    @param:Json(name = "freshmanQuota") val freshmanQuota: Long?,
    @param:Json(name = "remark") val remark: String,
    @param:Json(name = "category") val category: String?,
    @param:Json(name = "categoryPre2025") val categoryPre2025: String?,
    @param:Json(name = "colorIndex") val colorIndex: Long = 0, // 색상
    @param:Json(name = "color") val color: ColorDto = ColorDto(),
    @param:Json(name = "registrationCount") val registrationCount: Long = 0,
    @param:Json(name = "wasFull") val wasFull: Boolean = false,
    @param:Json(name = "snuttEvLecture") val review: LectureReviewDto? = null,
) {

    val isCustom: Boolean
        get() = course_number.isNullOrBlank() && lecture_number.isNullOrEmpty()

    companion object {
        val Default = LectureDto(
            id = "",
            course_title = "",
            instructor = "",
            colorIndex = 1L,
            color = ColorDto(),
            department = null,
            academic_year = null,
            credit = 0,
            category = null,
            categoryPre2025 = null,
            classification = null,
            course_number = null,
            lecture_number = null,
            quota = 0L,
            freshmanQuota = null,
            remark = "",
            class_time_json = emptyList(),
        )
    }
}
