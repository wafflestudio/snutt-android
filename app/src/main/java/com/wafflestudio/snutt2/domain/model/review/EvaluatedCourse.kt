package com.wafflestudio.snutt2.domain.model.review

import com.wafflestudio.snutt2.domain.model.Lecture

data class EvaluatedCourse(
    val courseId: Long,
    val lecture: Lecture,
    val averageRating: Double?,
    val reviewCount: Long,
    val averageMetrics: ReviewMetrics?,
)
