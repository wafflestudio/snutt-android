package com.wafflestudio.snutt2.domain.model

data class LectureReviewInfo(
    val courseId: String,
    val rating: Double?,
    val reviewCount: Int,
) {
    val ratingDisplayText get() = if (rating == null) "--" else "%.1f".format(rating)
    val displayText get() = "$ratingDisplayText ($reviewCount)"
}
