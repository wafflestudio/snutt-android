package com.wafflestudio.snutt2.views.logged_in.home.timetable.refactor

import com.wafflestudio.snutt2.domainmodel.CustomLecture
import com.wafflestudio.snutt2.domainmodel.CustomTheme
import com.wafflestudio.snutt2.domainmodel.LectureColor
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.LectureSession
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.domainmodel.ThemeColor
import java.time.DayOfWeek
import java.time.LocalTime

internal fun syllabusLecture(
    id: String,
    courseTitle: String,
    sessions: List<LectureSession>,
    instructor: String,
    color: LectureColor,
    lectureNumber: String = id.padStart(3, '0'),
) = SyllabusLecture(
    id = id,
    courseTitle = courseTitle,
    lectureSessions = sessions,
    instructor = instructor,
    credit = 3,
    remark = "",
    color = color,
    classification = "",
    department = "",
    academicYear = "",
    courseNumber = "",
    lectureNumber = lectureNumber,
    category = "",
    categoryPre2025 = "",
    quota = 0,
    freshmanQuota = 0,
    originalLectureId = "",
)

// 모든 강의가 BuiltIn 색인 사용
internal val builtInOnlyLectures: List<LocalLecture> = listOf(
    syllabusLecture(
        id = "1", courseTitle = "논리설계", instructor = "이창건",
        color = LectureColor.BuiltIn(0),
        sessions = listOf(
            LectureSession(null, DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(15, 15), "301-118"),
            LectureSession(null, DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(15, 15), "301-118"),
        ),
    ),
    syllabusLecture(
        id = "2", courseTitle = "이산수학", instructor = "김민수",
        color = LectureColor.BuiltIn(1),
        sessions = listOf(
            LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "302-208"),
            LectureSession(null, DayOfWeek.THURSDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "302-208"),
        ),
    ),
    syllabusLecture(
        id = "3", courseTitle = "대학 글쓰기 1", instructor = "박지영",
        color = LectureColor.BuiltIn(2),
        sessions = listOf(
            LectureSession(null, DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(11, 0), "5-302"),
        ),
    ),
    syllabusLecture(
        id = "4", courseTitle = "컴퓨터 프로그래밍", instructor = "홍길동",
        color = LectureColor.BuiltIn(3),
        sessions = listOf(
            LectureSession(null, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 15), "302-308"),
            LectureSession(null, DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(10, 15), "302-308"),
        ),
    ),
    syllabusLecture(
        id = "5", courseTitle = "통계학", instructor = "최수진",
        color = LectureColor.BuiltIn(4),
        sessions = listOf(
            LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "25-210"),
        ),
    ),
    syllabusLecture(
        id = "5", courseTitle = "통계학", instructor = "최수진",
        color = LectureColor.BuiltIn(4),
        sessions = listOf(),
    ),
)

// BuiltIn 색인 + Custom 색 혼합 (빌트인 테마에서 일부 강의만 사용자 커스텀 색)
internal val mixedColorLectures: List<LocalLecture> = listOf(
    syllabusLecture(
        id = "1", courseTitle = "논리설계", instructor = "이창건",
        color = LectureColor.BuiltIn(0),
        sessions = listOf(
            LectureSession(null, DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(15, 15), "301-118"),
            LectureSession(null, DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(15, 15), "301-118"),
        ),
    ),
    syllabusLecture(
        id = "2", courseTitle = "이산수학", instructor = "김민수",
        color = LectureColor.Custom(foreground = 0xFFFFFFFF.toInt(), background = 0xFF6172E9.toInt()),
        sessions = listOf(
            LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "302-208"),
            LectureSession(null, DayOfWeek.THURSDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "302-208"),
        ),
    ),
    syllabusLecture(
        id = "3", courseTitle = "대학 글쓰기 1", instructor = "박지영",
        color = LectureColor.BuiltIn(2),
        sessions = listOf(
            LectureSession(null, DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(11, 0), "5-302"),
        ),
    ),
    syllabusLecture(
        id = "4", courseTitle = "컴퓨터 프로그래밍", instructor = "홍길동",
        color = LectureColor.Custom(foreground = 0xFF1A1A1A.toInt(), background = 0xFFFFD700.toInt()),
        sessions = listOf(
            LectureSession(null, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 15), "302-308"),
            LectureSession(null, DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(10, 15), "302-308"),
        ),
    ),
    CustomLecture(
        id = "5", courseTitle = "스터디", instructor = "",
        credit = 0, remark = "",
        color = LectureColor.Custom(foreground = 0xFFFFFFFF.toInt(), background = 0xFF333333.toInt()),
        lectureSessions = listOf(
            LectureSession(null, DayOfWeek.MONDAY, LocalTime.of(19, 0), LocalTime.of(20, 30), "카페"),
        ),
    ),
)

// CustomTheme 샘플
internal val sampleCustomTheme = CustomTheme(
    id = "preview_custom_1",
    name = "파스텔",
    isFromMarket = false,
    colors = listOf(
        ThemeColor(foreground = 0xFF333333.toInt(), background = 0xFFFFB3BA.toInt()),
        ThemeColor(foreground = 0xFF333333.toInt(), background = 0xFFBAE1FF.toInt()),
        ThemeColor(foreground = 0xFF333333.toInt(), background = 0xFFBAFFBA.toInt()),
        ThemeColor(foreground = 0xFF333333.toInt(), background = 0xFFFFDFBA.toInt()),
        ThemeColor(foreground = 0xFF333333.toInt(), background = 0xFFE8BAFF.toInt()),
    ),
)

// 선택 강의 샘플
internal val sampleSelectedLecture = SearchedLecture(
    id = "search1",
    courseTitle = "알고리즘",
    lectureSessions = listOf(
        LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "302-208"),
        LectureSession(null, DayOfWeek.THURSDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "302-208"),
    ),
    instructor = "이정우",
    credit = 3,
    remark = "",
    classification = "",
    department = "",
    academicYear = "",
    courseNumber = "",
    lectureNumber = "001",
    category = "",
    categoryPre2025 = "",
    quota = 60,
    freshmanQuota = 0,
    registrationCount = 45,
    wasFull = false,
    reviewInfo = LectureReviewInfo("", 0.0, 0),
)
