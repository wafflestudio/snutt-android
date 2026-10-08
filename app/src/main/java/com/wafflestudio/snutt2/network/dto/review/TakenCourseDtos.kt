package com.wafflestudio.snutt2.network.dto.review

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LatestTakenCoursesDto(
    val content: List<TakenCourseDto>,
    @param:Json(name = "total_count") val totalCount: Int,
)

@JsonClass(generateAdapter = true)
data class TakenCourseDto(
    val id: Long? = null,
    val title: String,
    val instructor: String,
    val department: String? = null,
    @param:Json(name = "course_number") val courseNumber: String,
    val credit: Int? = null,
    @param:Json(name = "academic_year") val academicYear: String? = null,
    val category: String? = null,
    val classification: String? = null,
    @param:Json(name = "taken_year") val takenYear: Int,
    @param:Json(name = "taken_semester") val takenSemester: Int,
)
