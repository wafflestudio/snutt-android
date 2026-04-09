package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// 신규
@JsonClass(generateAdapter = true)
data class TimetableLectureDto(
    @param:Json(name = "id") val id: String?,
    @param:Json(name = "academicYear") val academicYear: String?,
    @param:Json(name = "category") val category: String?,
    @param:Json(name = "classPlaceAndTimes") val classPlaceAndTimes: List<ClassPlaceAndTimeDto>,
    @param:Json(name = "classification") val classification: String?,
    @param:Json(name = "credit") val credit: Long?,
    @param:Json(name = "department") val department: String?,
    @param:Json(name = "instructor") val instructor: String?,
    @param:Json(name = "lectureNumber") val lectureNumber: String?,
    @param:Json(name = "quota") val quota: Int?,
    @param:Json(name = "freshmanQuota") val freshmanQuota: Int?,
    @param:Json(name = "remark") val remark: String?,
    @param:Json(name = "courseNumber") val courseNumber: String?,
    @param:Json(name = "courseTitle") val courseTitle: String,
    @param:Json(name = "color") val color: ColorSetDto?,
    @param:Json(name = "colorIndex") val colorIndex: Int,
    @param:Json(name = "lectureId") val lectureId: String?,
    @param:Json(name = "snuttEvLecture") val snuttEvLecture: SnuttEvLectureIdDto?,
    @param:Json(name = "categoryPre2025") val categoryPre2025: String?,
) {
    val isCustom: Boolean
        get() = courseNumber.isNullOrBlank() && lectureNumber.isNullOrEmpty()

    companion object {
        val Default = TimetableLectureDto(
            id = "",
            courseTitle = "",
            instructor = "",
            colorIndex = 1,
            color = null,
            department = null,
            academicYear = null,
            credit = 0,
            category = null,
            categoryPre2025 = null,
            classification = null,
            courseNumber = null,
            lectureNumber = null,
            quota = 0,
            freshmanQuota = null,
            remark = null,
            classPlaceAndTimes = emptyList(),
            lectureId = null,
            snuttEvLecture = null,
        )
    }
}
