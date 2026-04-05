package com.wafflestudio.snutt2.fixture

import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.SearchedLecture

object TestFixtures {

    fun searchedLecture(
        id: String = "lec-1",
        courseTitle: String = "컴퓨터개론",
        registrationCount: Long = 10,
        wasFull: Boolean = false,
    ) = SearchedLecture(
        id = id,
        courseTitle = courseTitle,
        lectureSessions = emptyList(),
        instructor = "",
        credit = 3,
        remark = "",
        classification = "",
        department = "",
        academicYear = "",
        courseNumber = "",
        lectureNumber = "",
        category = "",
        categoryPre2025 = "",
        quota = 30,
        freshmanQuota = 0,
        registrationCount = registrationCount,
        wasFull = wasFull,
        reviewInfo = LectureReviewInfo(id = "", rating = null, reviewCount = 0),
    )

    val lecture1 = searchedLecture(id = "lec-1", courseTitle = "컴퓨터개론")
    val lecture2 = searchedLecture(id = "lec-2", courseTitle = "자료구조")
}
