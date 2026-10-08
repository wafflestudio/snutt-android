package com.wafflestudio.snutt2.ui.preview

import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.LectureReviewInfo
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.review.EvaluatedCourse
import com.wafflestudio.snutt2.domain.model.review.LectureReview
import com.wafflestudio.snutt2.domain.model.review.ReviewMetrics

object ReviewPreviewData {
    val metrics = ReviewMetrics(gradeSatisfaction = 2.0, gains = 5.0, teachingSkill = 4.5, lifeBalance = 3.5)

    private val lecture = SearchedLecture(
        id = "lecture-1",
        courseTitle = "인공지능 신뢰성",
        lectureSessions = emptyList(),
        instructor = "박상철",
        credit = 3,
        remark = "",
        classification = "전선",
        department = "협동과정 인공지능전공",
        academicYear = "",
        courseNumber = "",
        lectureNumber = "",
        category = "전선",
        categoryPre2025 = "",
        quota = 0,
        freshmanQuota = 0,
        registrationCount = 0,
        wasFull = false,
        reviewInfo = LectureReviewInfo(courseId = "1", rating = 3.0, reviewCount = 3),
    )

    val course = EvaluatedCourse(
        courseId = 1,
        lecture = lecture,
        averageRating = 3.0,
        reviewCount = 3,
        averageMetrics = ReviewMetrics(gradeSatisfaction = 2.0, gains = 4.5, teachingSkill = 3.5, lifeBalance = 5.0),
    )

    val reviews = listOf(
        LectureReview(
            id = 1,
            course = course,
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
            course = course.copy(lecture = lecture.copy(courseTitle = "모빌리티디자인프로젝트", instructor = "교수명")),
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
            lecture = lecture.copy(courseTitle = "인공지능과 생활", instructor = "교수님", reviewInfo = lecture.reviewInfo.copy(courseId = "2")),
            averageRating = null,
            reviewCount = 0,
        ),
        course.copy(
            courseId = 3,
            lecture = lecture.copy(courseTitle = "인공지능", instructor = "교수명", reviewInfo = lecture.reviewInfo.copy(courseId = "3")),
            averageRating = 4.6,
        ),
    )

    val semesterGroups = linkedMapOf(
        CourseBook(semester = 3, year = 2025) to reviews,
        CourseBook(semester = 1, year = 2025) to reviews.take(1),
    )
}
