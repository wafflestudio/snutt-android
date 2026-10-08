package com.wafflestudio.snutt2.ui.preview

import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.review.LectureReview
import com.wafflestudio.snutt2.domain.model.review.ReviewMetrics
import com.wafflestudio.snutt2.domain.model.review.ReviewedCourse

object ReviewPreviewData {
    val metrics = ReviewMetrics(gradeSatisfaction = 2.0, gains = 5.0, teachingSkill = 4.5, lifeBalance = 3.5)

    val course = ReviewedCourse(
        courseId = 1,
        title = "인공지능 신뢰성",
        instructor = "박상철",
        department = "협동과정 인공지능전공",
        category = "전선",
        credit = 3,
        averageRating = 3.0,
        reviewCount = 3,
        averageMetrics = ReviewMetrics(gradeSatisfaction = 2.0, gains = 4.5, teachingSkill = 3.5, lifeBalance = 5.0),
    )

    val reviews = listOf(
        LectureReview(
            id = 1,
            courseId = course.courseId,
            courseTitle = course.title,
            instructor = course.instructor,
            courseBook = CourseBook(semester = 3, year = 2025),
            rating = 3.0,
            metrics = metrics,
            content = "강의평 내용을 입력하세요. 강의평 내용을 입력하세요. 강의평 내용을 입력하세요. 강의평 내용을 입력하세요.",
            likeCount = 12,
            isLiked = false,
            isModifiable = false,
            isReportable = true,
        ),
        LectureReview(
            id = 2,
            courseId = 2,
            courseTitle = "인공지능과 생활",
            instructor = "교수님",
            courseBook = CourseBook(semester = 3, year = 2025),
            rating = 3.5,
            metrics = metrics,
            content = "강의평 내용을 입력하세요. 강의평 내용을 입력하세요. 강의평 내용을 입력하세요.",
            likeCount = 12,
            isLiked = false,
            isModifiable = false,
            isReportable = true,
        ),
    )

    val searchResults = listOf(
        course,
        course.copy(
            courseId = 2,
            title = "인공지능과 생활",
            instructor = "교수님",
            averageRating = 3.5,
            reviewCount = 1,
            averageMetrics = metrics,
        ),
        course.copy(
            courseId = 3,
            title = "인공지능",
            instructor = "교수명",
            averageRating = 4.6,
        ),
    )

    val semesterGroups = listOf(
        reviews[0],
        reviews[1].copy(courseBook = CourseBook(semester = 1, year = 2025)),
    ).groupBy { it.courseBook }
}
