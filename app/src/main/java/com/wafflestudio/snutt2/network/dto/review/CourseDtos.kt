package com.wafflestudio.snutt2.network.dto.review

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CourseSearchPageDto(
    val content: List<CourseSearchItemDto>,
    val page: Int,
    val size: Int,
    val last: Boolean,
    @param:Json(name = "total_count") val totalCount: Long,
)

@JsonClass(generateAdapter = true)
data class CourseSearchItemDto(
    val id: Long? = null,
    val title: String,
    val instructor: String,
    val department: String? = null,
    @param:Json(name = "course_number") val courseNumber: String,
    val credit: Int? = null,
    @param:Json(name = "academic_year") val academicYear: String? = null,
    val category: String? = null,
    val classification: String? = null,
    @param:Json(name = "evaluation") val reviewSummary: CourseRatingSummaryDto,
)

@JsonClass(generateAdapter = true)
data class CourseWithSemesterLecturesDto(
    val id: Long? = null,
    val title: String,
    val instructor: String,
    val department: String? = null,
    @param:Json(name = "course_number") val courseNumber: String,
    val credit: Int? = null,
    @param:Json(name = "academic_year") val academicYear: String? = null,
    val category: String? = null,
    val classification: String? = null,
    @param:Json(name = "semester_lectures") val semesterLectures: List<SemesterLectureDto>,
)

@JsonClass(generateAdapter = true)
data class SemesterLectureDto(
    val id: Long,
    val year: Int,
    val semester: Int,
    val credit: Int,
    @param:Json(name = "extra_info") val extraInfo: String,
    @param:Json(name = "academic_year") val academicYear: String,
    val category: String,
    val classification: String,
    @param:Json(name = "my_evaluation_exists") val myReviewExists: Boolean,
)

@JsonClass(generateAdapter = true)
data class CourseRatingSummaryDto(
    @param:Json(name = "avg_rating") val avgRating: Double? = null,
    @param:Json(name = "evaluation_count") val reviewCount: Long,
)

@JsonClass(generateAdapter = true)
data class ScoreAveragesDto(
    @param:Json(name = "avg_grade_satisfaction") val avgGradeSatisfaction: Double? = null,
    @param:Json(name = "avg_teaching_skill") val avgTeachingSkill: Double? = null,
    @param:Json(name = "avg_gains") val avgGains: Double? = null,
    @param:Json(name = "avg_life_balance") val avgLifeBalance: Double? = null,
    @param:Json(name = "avg_rating") val avgRating: Double? = null,
    @param:Json(name = "evaluation_count") val reviewCount: Long,
)

@JsonClass(generateAdapter = true)
data class CourseReviewSummaryDto(
    val id: Long? = null,
    val title: String,
    val instructor: String? = null,
    val department: String? = null,
    @param:Json(name = "course_number") val courseNumber: String,
    val credit: Int? = null,
    @param:Json(name = "academic_year") val academicYear: String? = null,
    val category: String? = null,
    val classification: String? = null,
    @param:Json(name = "evaluation") val reviewAverages: ScoreAveragesDto,
)

@JsonClass(generateAdapter = true)
data class CourseIdLookupDto(
    val id: Long,
    val snuttId: String? = null,
    val evLectureId: Long,
)
