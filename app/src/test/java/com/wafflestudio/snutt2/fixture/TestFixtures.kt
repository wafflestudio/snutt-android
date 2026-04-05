package com.wafflestudio.snutt2.fixture

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.domainmodel.TableSummary
import com.wafflestudio.snutt2.domainmodel.ThemeReference

object TestFixtures {

    // --- CourseBook ---

    val courseBook2025_1 = CourseBook(semester = 1, year = 2025)
    val courseBook2024_2 = CourseBook(semester = 2, year = 2024)

    // --- TableSummary ---

    fun tableSummary(
        id: String = "table-1",
        courseBook: CourseBook = courseBook2025_1,
        title: String = "��의 시간표",
        totalCredit: Long = 18,
        isPrimary: Boolean = false,
    ) = TableSummary(
        id = id,
        courseBook = courseBook,
        title = title,
        totalCredit = totalCredit,
        isPrimary = isPrimary,
    )

    // --- Table ---

    fun table(
        summary: TableSummary = tableSummary(),
        themeRef: ThemeReference = ThemeReference.BuiltIn(0),
    ) = Table(
        summary = summary,
        lectures = emptyList(),
        themeRef = themeRef,
    )

    // --- SearchedLecture ---

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
