package com.wafflestudio.snutt2.domain.model.review

import com.wafflestudio.snutt2.domain.model.CourseBook

data class LectureReview(
    val id: Long,
    val course: EvaluatedCourse,
    val courseBook: CourseBook,
    val rating: Double,
    val metrics: ReviewMetrics,
    val content: String,
    val likeCount: Long,
    val isLiked: Boolean,
    val isModifiable: Boolean,
    val isReportable: Boolean,
)
