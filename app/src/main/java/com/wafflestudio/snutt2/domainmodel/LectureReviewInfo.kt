package com.wafflestudio.snutt2.domainmodel

data class LectureReviewInfo(
    val id: String,
    val rating: Double?,
    val reviewCount: Int,
) {
    val ratingDisplayText get() = if(rating == null) "--" else "%.1f".format(rating)
    val displayText get() = "$ratingDisplayText (${reviewCount})"
}
