package com.wafflestudio.snutt2.ui.preview

import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.domain.model.LectureReviewInfo
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.SearchTag
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.SyllabusLecture
import com.wafflestudio.snutt2.domain.model.TagType
import com.wafflestudio.snutt2.domain.model.ThemeColor
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
val builtInOnlyLectures: List<LocalLecture> = listOf(
    syllabusLecture(
        id = "1",
        courseTitle = "논리설계",
        instructor = "이창건",
        color = LectureColor.BuiltIn(0),
        sessions = listOf(
            LectureSession(null, DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(15, 15), "301-118"),
            LectureSession(null, DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(15, 15), "301-118"),
        ),
    ),
    syllabusLecture(
        id = "2",
        courseTitle = "이산수학",
        instructor = "김민수",
        color = LectureColor.BuiltIn(1),
        sessions = listOf(
            LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "302-208"),
            LectureSession(null, DayOfWeek.THURSDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "302-208"),
        ),
    ),
    syllabusLecture(
        id = "3",
        courseTitle = "대학 글쓰기 1",
        instructor = "박지영",
        color = LectureColor.BuiltIn(2),
        sessions = listOf(
            LectureSession(null, DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(11, 0), "5-302"),
        ),
    ),
    syllabusLecture(
        id = "4",
        courseTitle = "컴퓨터 프로그래밍",
        instructor = "홍길동",
        color = LectureColor.BuiltIn(3),
        sessions = listOf(
            LectureSession(null, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 15), "302-308"),
            LectureSession(null, DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(10, 15), "302-308"),
        ),
    ),
    syllabusLecture(
        id = "5",
        courseTitle = "통계학",
        instructor = "최수진",
        color = LectureColor.BuiltIn(4),
        sessions = listOf(
            LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "25-210"),
        ),
    ),
    syllabusLecture(
        id = "5",
        courseTitle = "통계학",
        instructor = "최수진",
        color = LectureColor.BuiltIn(4),
        sessions = listOf(),
    ),
)

// BuiltIn 위주이지만 커스텀 색상 강의가 하나 섞인 케이스 (실제 사용 패턴: 빌트인 테마 + 일부 강의에 사용자 커스텀 색 지정)
val builtInWithOneCustomLecture: List<LocalLecture> = listOf(
    syllabusLecture(
        id = "1",
        courseTitle = "논리설계",
        instructor = "이창건",
        color = LectureColor.BuiltIn(0),
        sessions = listOf(
            LectureSession(null, DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(15, 15), "301-118"),
            LectureSession(null, DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(15, 15), "301-118"),
        ),
    ),
    syllabusLecture(
        id = "2",
        courseTitle = "이산수학",
        instructor = "김민수",
        color = LectureColor.BuiltIn(1),
        sessions = listOf(
            LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "302-208"),
            LectureSession(null, DayOfWeek.THURSDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "302-208"),
        ),
    ),
    syllabusLecture(
        id = "3",
        courseTitle = "대학 글쓰기 1",
        instructor = "박지영",
        color = LectureColor.BuiltIn(2),
        sessions = listOf(
            LectureSession(null, DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(11, 0), "5-302"),
        ),
    ),
    syllabusLecture(
        id = "4",
        courseTitle = "컴퓨터 프로그래밍",
        instructor = "홍길동",
        color = LectureColor.BuiltIn(3),
        sessions = listOf(
            LectureSession(null, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 15), "302-308"),
            LectureSession(null, DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(10, 15), "302-308"),
        ),
    ),
    // 한 강의만 사용자 커스텀 색
    syllabusLecture(
        id = "5",
        courseTitle = "통계학",
        instructor = "최수진",
        color = LectureColor.Custom(foreground = 0xFFFFFFFF.toInt(), background = 0xFF6172E9.toInt()),
        sessions = listOf(
            LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "25-210"),
        ),
    ),
)

// CustomTheme 샘플
val sampleCustomTheme = CustomTheme(
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
val sampleSelectedLecture = SearchedLecture(
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


val previewTagTypes = listOf(
    TagType.SORT_CRITERIA,
    TagType.CLASSIFICATION,
    TagType.DEPARTMENT,
    TagType.ACADEMIC_YEAR,
    TagType.CREDIT,
    TagType.TIME,
    TagType.ETC,
)

val previewAllTags = listOf(
    SearchTag.Regular(TagType.SORT_CRITERIA, "평점 높은 순"),
    SearchTag.Regular(TagType.SORT_CRITERIA, "강의평 많은 순"),
    SearchTag.Regular(TagType.CLASSIFICATION, "공통"),
    SearchTag.Regular(TagType.CLASSIFICATION, "교양"),
    SearchTag.Regular(TagType.CLASSIFICATION, "논문"),
    SearchTag.Regular(TagType.CLASSIFICATION, "일선"),
    SearchTag.Regular(TagType.CLASSIFICATION, "전선"),
    SearchTag.Regular(TagType.CLASSIFICATION, "전필"),
    SearchTag.Regular(TagType.DEPARTMENT, "컴퓨터공학부"),
    SearchTag.Regular(TagType.DEPARTMENT, "전기정보공학부"),
    SearchTag.Regular(TagType.DEPARTMENT, "기계공학부"),
    SearchTag.Regular(TagType.ACADEMIC_YEAR, "1학년"),
    SearchTag.Regular(TagType.ACADEMIC_YEAR, "2학년"),
    SearchTag.Regular(TagType.ACADEMIC_YEAR, "3학년"),
    SearchTag.Regular(TagType.CREDIT, "1학점"),
    SearchTag.Regular(TagType.CREDIT, "2학점"),
    SearchTag.Regular(TagType.CREDIT, "3학점"),
    SearchTag.TimeEmpty,
    SearchTag.TimeSelect,
    SearchTag.EtcEng,
    SearchTag.EtcMilitary,
)
