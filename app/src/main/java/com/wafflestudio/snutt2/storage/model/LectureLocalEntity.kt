package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LectureLocalEntity(
    @param:Json(name = "_id") val id: String,
    @param:Json(name = "lecture_id") val lecture_id: String? = null,
    @param:Json(name = "classification") val classification: String?,
    @param:Json(name = "department") val department: String?,
    @param:Json(name = "academic_year") val academic_year: String?,
    @param:Json(name = "course_number") val course_number: String?,
    @param:Json(name = "lecture_number") val lecture_number: String?,
    @param:Json(name = "course_title") val course_title: String,
    @param:Json(name = "credit") val credit: Long,
    @param:Json(name = "class_time_json") val class_time_json: List<ClassTimeLocalEntity>,
    @param:Json(name = "instructor") val instructor: String,
    @param:Json(name = "quota") val quota: Long = 0,
    @param:Json(name = "freshmanQuota") val freshmanQuota: Long?,
    @param:Json(name = "remark") val remark: String,
    @param:Json(name = "category") val category: String?,
    @param:Json(name = "categoryPre2025") val categoryPre2025: String?,
    @param:Json(name = "colorIndex") val colorIndex: Long = 0,
    @param:Json(name = "color") val color: ColorLocalEntity = ColorLocalEntity(),
    @param:Json(name = "registrationCount") val registrationCount: Long = 0,
    @param:Json(name = "wasFull") val wasFull: Boolean = false,
    @param:Json(name = "snuttEvLecture") val review: LectureReviewLocalEntity? = null,
)
