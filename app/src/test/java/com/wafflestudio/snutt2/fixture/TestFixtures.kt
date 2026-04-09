package com.wafflestudio.snutt2.fixture

import com.wafflestudio.snutt2.domain.model.Building
import com.wafflestudio.snutt2.domain.model.Campus
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.domain.model.GeoCoordinate
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.domain.model.LectureReviewInfo
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.SyllabusLecture
import com.wafflestudio.snutt2.domain.model.Table
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.domain.model.ThemeColor
import com.wafflestudio.snutt2.domain.model.ThemeReference

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

    // --- SyllabusLecture ---

    fun syllabusLecture(
        id: String = "syllabus-1",
        courseTitle: String = "컴퓨터개론",
        originalLectureId: String = "lec-1",
        color: LectureColor = LectureColor.BuiltIn(0),
    ) = SyllabusLecture(
        id = id,
        courseTitle = courseTitle,
        lectureSessions = emptyList(),
        instructor = "",
        credit = 3,
        remark = "",
        color = color,
        classification = "",
        department = "",
        academicYear = "",
        courseNumber = "",
        lectureNumber = "",
        category = "",
        categoryPre2025 = "",
        quota = 30,
        freshmanQuota = 0,
        originalLectureId = originalLectureId,
    )

    // --- Building ---

    fun building(
        buildingNumber: String = "301",
        buildingNameKor: String = "제1공학관",
        campus: Campus = Campus.GWANAK,
    ) = Building(
        campus = campus,
        buildingNumber = buildingNumber,
        buildingNameKor = buildingNameKor,
        buildingNameEng = "Engineering Building 1",
        coordinate = GeoCoordinate(latitude = 37.4563, longitude = 126.9520),
    )

    // --- CustomTheme ---

    fun customTheme(
        id: String = "theme-1",
        name: String = "내 테마",
        isFromMarket: Boolean = false,
    ) = CustomTheme(
        id = id,
        name = name,
        isFromMarket = isFromMarket,
        colors = listOf(ThemeColor(0xFFFFFFFF.toInt(), 0xFF000000.toInt())),
    )
}
