package com.wafflestudio.snutt2.domain.model.review

data class ReviewedCourse(
    val courseId: Long,
    val title: String,
    val instructor: String,
    val department: String?,
    val category: String?,
    val credit: Int?,
    val averageRating: Double?,
    val reviewCount: Long,
    val averageMetrics: ReviewMetrics?,
)
