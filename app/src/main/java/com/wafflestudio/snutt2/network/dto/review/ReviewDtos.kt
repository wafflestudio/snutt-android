package com.wafflestudio.snutt2.network.dto.review

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReviewWithCourseIdPageDto(
    val content: List<ReviewWithCourseIdDto>,
    val cursor: String? = null,
    val size: Int,
    val last: Boolean,
    @param:Json(name = "total_count") val totalCount: Long? = null,
)

@JsonClass(generateAdapter = true)
data class ReviewWithCourseBriefPageDto(
    val content: List<ReviewWithCourseBriefDto>,
    val cursor: String? = null,
    val size: Int,
    val last: Boolean,
    @param:Json(name = "total_count") val totalCount: Long? = null,
)

@JsonClass(generateAdapter = true)
data class ReviewWithCourseIdDto(
    val id: Long? = null,
    @param:Json(name = "user_id") val userId: String? = null,
    val content: String,
    @param:Json(name = "grade_satisfaction") val gradeSatisfaction: Double? = null,
    @param:Json(name = "teaching_skill") val teachingSkill: Double? = null,
    val gains: Double? = null,
    @param:Json(name = "life_balance") val lifeBalance: Double? = null,
    val rating: Double,
    @param:Json(name = "like_count") val likeCount: Long,
    @param:Json(name = "is_hidden") val isHidden: Boolean,
    @param:Json(name = "is_reported") val isReported: Boolean,
    @param:Json(name = "is_liked") val isLiked: Boolean,
    @param:Json(name = "from_snuev") val fromSnuev: Boolean,
    val year: Int,
    val semester: Int,
    @param:Json(name = "lecture_id") val courseId: Long,
    @param:Json(name = "is_modifiable") val isModifiable: Boolean,
    @param:Json(name = "is_reportable") val isReportable: Boolean,
)

@JsonClass(generateAdapter = true)
data class ReviewWithCourseBriefDto(
    val id: Long? = null,
    @param:Json(name = "user_id") val userId: String? = null,
    val content: String,
    @param:Json(name = "grade_satisfaction") val gradeSatisfaction: Double? = null,
    @param:Json(name = "teaching_skill") val teachingSkill: Double? = null,
    val gains: Double? = null,
    @param:Json(name = "life_balance") val lifeBalance: Double? = null,
    val rating: Double,
    @param:Json(name = "like_count") val likeCount: Long,
    @param:Json(name = "is_hidden") val isHidden: Boolean,
    @param:Json(name = "is_reported") val isReported: Boolean,
    @param:Json(name = "is_liked") val isLiked: Boolean,
    @param:Json(name = "from_snuev") val fromSnuev: Boolean,
    val year: Int,
    val semester: Int,
    @param:Json(name = "lecture") val course: CourseBriefDto? = null,
    @param:Json(name = "is_modifiable") val isModifiable: Boolean,
    @param:Json(name = "is_reportable") val isReportable: Boolean,
)

@JsonClass(generateAdapter = true)
data class CourseBriefDto(
    val id: Long? = null,
    val title: String,
    val instructor: String,
)

@JsonClass(generateAdapter = true)
data class MyCourseReviewsDto(
    @param:Json(name = "evaluations") val reviews: List<ReviewWithCourseIdDto>,
)

@JsonClass(generateAdapter = true)
data class PostReviewParams(
    val content: String,
    @param:Json(name = "grade_satisfaction") val gradeSatisfaction: Double,
    @param:Json(name = "teaching_skill") val teachingSkill: Double,
    val gains: Double,
    @param:Json(name = "life_balance") val lifeBalance: Double,
    val rating: Double,
)

@JsonClass(generateAdapter = true)
data class PostReviewResults(
    val id: Long? = null,
    @param:Json(name = "user_id") val userId: String? = null,
    val content: String,
    @param:Json(name = "grade_satisfaction") val gradeSatisfaction: Double? = null,
    @param:Json(name = "teaching_skill") val teachingSkill: Double? = null,
    val gains: Double? = null,
    @param:Json(name = "life_balance") val lifeBalance: Double? = null,
    val rating: Double,
    @param:Json(name = "like_count") val likeCount: Long,
    @param:Json(name = "is_hidden") val isHidden: Boolean,
    @param:Json(name = "is_reported") val isReported: Boolean,
    @param:Json(name = "from_snuev") val fromSnuev: Boolean,
)

@JsonClass(generateAdapter = true)
data class PatchReviewParams(
    val content: String? = null,
    @param:Json(name = "grade_satisfaction") val gradeSatisfaction: Double? = null,
    @param:Json(name = "teaching_skill") val teachingSkill: Double? = null,
    val gains: Double? = null,
    @param:Json(name = "life_balance") val lifeBalance: Double? = null,
    val rating: Double? = null,
    @param:Json(name = "semester_lecture_id") val semesterLectureId: String? = null,
)

@JsonClass(generateAdapter = true)
data class PostReviewReportParams(
    val content: String,
)

@JsonClass(generateAdapter = true)
data class PostReviewReportResults(
    val id: Long? = null,
    @param:Json(name = "lecture_evaluation_id") val reviewId: Long,
    @param:Json(name = "user_id") val userId: String? = null,
    val content: String,
    @param:Json(name = "is_hidden") val isHidden: Boolean,
)
