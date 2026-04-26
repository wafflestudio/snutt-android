package com.wafflestudio.snutt2.ui.preview

import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.TableSummary

object TableSummaryPreviewData {
    val drawerPrimaryTable = TableSummary(
        id = "drawer_table_primary",
        courseBook = CourseBook(semester = 1, year = 2026),
        title = "2026-1학기 메인",
        totalCredit = 18,
        isPrimary = true,
    )

    val drawerSecondaryTable = TableSummary(
        id = "drawer_table_secondary",
        courseBook = CourseBook(semester = 1, year = 2026),
        title = "2026-1학기 부전공 플랜",
        totalCredit = 15,
        isPrimary = false,
    )

    val drawerLongTitleTable = TableSummary(
        id = "drawer_table_long_title",
        courseBook = CourseBook(semester = 1, year = 2026),
        title = "엄청나게 긴 시간표 이름 케이스 ellipsis 확인용 시간표",
        totalCredit = 21,
        isPrimary = false,
    )

    val drawerLastSemesterTable = TableSummary(
        id = "drawer_table_last_semester",
        courseBook = CourseBook(semester = 2, year = 2025),
        title = "2025-2학기 시간표",
        totalCredit = 17,
        isPrimary = true,
    )

    val sampleCourseBooks = listOf(
        CourseBook(semester = 1, year = 2025),
        CourseBook(semester = 2, year = 2024),
        CourseBook(semester = 1, year = 2024),
    )
}
