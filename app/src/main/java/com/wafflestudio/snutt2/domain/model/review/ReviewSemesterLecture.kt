package com.wafflestudio.snutt2.domain.model.review

import com.wafflestudio.snutt2.domain.model.CourseBook

data class ReviewSemesterLecture(
    val semesterLectureId: Long,
    val courseBook: CourseBook,
    val hasMyReview: Boolean,
)
