package com.wafflestudio.snutt2.ui.preview

import com.wafflestudio.snutt2.domain.model.CustomLecture
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset
import com.wafflestudio.snutt2.domain.model.LectureReviewInfo
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.SyllabusLecture
import java.time.DayOfWeek
import java.time.LocalTime

object LecturePreviewData {
    private val customColor1 = LectureColor.Custom(
        foreground = 0xFF3B41FF.toInt(),
        background = 0xFFCD4A2E.toInt(),
    )

    private val sampleSessions = listOf(
        LectureSession(
            id = null,
            day = DayOfWeek.MONDAY,
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 15),
            place = "301-118",
        ),
        LectureSession(
            id = null,
            day = DayOfWeek.MONDAY,
            startTime = LocalTime.of(19, 0),
            endTime = LocalTime.of(20, 50),
            place = "302-310-2",
        ),
        LectureSession(
            id = null,
            day = DayOfWeek.WEDNESDAY,
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 15),
            place = "301-118",
        ),
    )

    val syllabusLecture = SyllabusLecture(
        id = "67dff853fc174d776c66b27b",
        courseTitle = "논리설계",
        lectureSessions = sampleSessions,
        instructor = "이창건",
        credit = 4,
        remark = "ⓔ(수강신청 1~4일: 2/7~2/10) 컴퓨터공학부 주전공(자유전공학부(컴공 주전공) 포함)만 신청 가능 (수강신청 5일차부터: 2/11) 컴퓨터공학부 주전공(자유전공학부(컴공 주전공) 포함), 컴퓨터공학부 복수전공, 부전공, 연합전공 인공지능, 연합전공 인공지능 반도체공학 전공만 신청가능 (수강신청 6일차부터: 2/14) 전체 학생 신청 가능 (정원내 신청 가능)",
        color = customColor1,
        classification = "전필",
        department = "컴퓨터공학부",
        academicYear = "2학년",
        courseNumber = "M1522.000700",
        lectureNumber = "001",
        category = "",
        categoryPre2025 = "",
        quota = 60,
        freshmanQuota = 30,
        originalLectureId = "61e4c9437d86910064ed373a",
    )

    private fun previewSession(
        day: DayOfWeek,
        place: String,
        startMinute: Int,
        endMinute: Int,
    ) = LectureSession(
        id = null,
        day = day,
        startTime = LocalTime.ofSecondOfDay(startMinute * 60L),
        endTime = LocalTime.ofSecondOfDay(endMinute * 60L),
        place = place,
    )

    val sampleLectures: List<SearchedLecture> = listOf(
        SearchedLecture(
            id = "6896f09f72111e64aa138f8b",
            courseTitle = " 스튜디오 뮤직 메이킹 (로직, 거라지밴드) 입문 ",
            lectureSessions = listOf(
                previewSession(DayOfWeek.MONDAY, "220-413", 780, 950),
            ),
            instructor = "이지수",
            credit = 2,
            remark = "",
            classification = "전선",
            department = "음악학과",
            academicYear = "1학년",
            courseNumber = "M2183.003400",
            lectureNumber = "001",
            category = "",
            categoryPre2025 = "",
            quota = 10,
            freshmanQuota = 0,
            registrationCount = 10,
            wasFull = false,
            reviewInfo = LectureReviewInfo(id = "49367", rating = null, reviewCount = 0),
        ),
        SearchedLecture(
            id = "6896f0a272111e64aa138f8c",
            courseTitle = " 글로벌 공학기술 교류 특강 2 (국제 물류)",
            lectureSessions = listOf(
                previewSession(DayOfWeek.MONDAY, "38-B105", 630, 740),
            ),
            instructor = "박건수",
            credit = 2,
            remark = "ⓔ",
            classification = "전선",
            department = "공과대학",
            academicYear = "석박사통합",
            courseNumber = "M2177.005600",
            lectureNumber = "001",
            category = "",
            categoryPre2025 = "",
            quota = 30,
            freshmanQuota = 0,
            registrationCount = 0,
            wasFull = false,
            reviewInfo = LectureReviewInfo(id = "43308", rating = null, reviewCount = 0),
        ),
        SearchedLecture(
            id = "6896f0a372111e64aa138f8d",
            courseTitle = "(공유)NPU 기반 인공지능 추론 및 응용",
            lectureSessions = listOf(
                previewSession(DayOfWeek.THURSDAY, "301-B119", 720, 890),
            ),
            instructor = "윤정남",
            credit = 3,
            remark = "",
            classification = "전선",
            department = "혁신공유학부",
            academicYear = "2학년",
            courseNumber = "M3502.016700",
            lectureNumber = "001",
            category = "",
            categoryPre2025 = "",
            quota = 30,
            freshmanQuota = 0,
            registrationCount = 0,
            wasFull = false,
            reviewInfo = LectureReviewInfo(id = "59313", rating = null, reviewCount = 0),
        ),
        SearchedLecture(
            id = "6896f0a572111e64aa138f8e",
            courseTitle = "(공유)공학 지식 및 실무",
            lectureSessions = listOf(
                previewSession(DayOfWeek.WEDNESDAY, "301-B119", 720, 770),
                previewSession(DayOfWeek.WEDNESDAY, "301-B119", 780, 1010),
            ),
            instructor = "이재학",
            credit = 3,
            remark = "",
            classification = "전선",
            department = "혁신공유학부",
            academicYear = "3학년",
            courseNumber = "M3502.001700",
            lectureNumber = "002",
            category = "",
            categoryPre2025 = "",
            quota = 30,
            freshmanQuota = 0,
            registrationCount = 0,
            wasFull = false,
            reviewInfo = LectureReviewInfo(id = "47436", rating = null, reviewCount = 0),
        ),
        SearchedLecture(
            id = "6896f0a772111e64aa138f8f",
            courseTitle = "(공유)에너지신산업 캡스톤디자인 2",
            lectureSessions = listOf(
                previewSession(DayOfWeek.FRIDAY, "38-418", 600, 770),
            ),
            instructor = "김수현",
            credit = 3,
            remark = "",
            classification = "전선",
            department = "혁신공유학부",
            academicYear = "4학년",
            courseNumber = "M3500.010700",
            lectureNumber = "001",
            category = "",
            categoryPre2025 = "",
            quota = 30,
            freshmanQuota = 0,
            registrationCount = 0,
            wasFull = false,
            reviewInfo = LectureReviewInfo(id = "47797", rating = null, reviewCount = 0),
        ),
        SearchedLecture(
            id = "6896f0bf72111e64aa138f90",
            courseTitle = "커스텀 1",
            lectureSessions = listOf(
                previewSession(DayOfWeek.TUESDAY, "", 570, 645),
            ),
            instructor = "",
            credit = 2,
            remark = "",
            classification = "",
            department = "",
            academicYear = "",
            courseNumber = "",
            lectureNumber = "",
            category = "",
            categoryPre2025 = "",
            quota = 0,
            freshmanQuota = 0,
            registrationCount = 0,
            wasFull = false,
            reviewInfo = LectureReviewInfo(id = "", rating = null, reviewCount = 0),
        ),
        SearchedLecture(
            id = "6896f10272111e64aa138f91",
            courseTitle = "커스텀 2",
            lectureSessions = listOf(
                previewSession(DayOfWeek.TUESDAY, "", 690, 765),
                previewSession(DayOfWeek.TUESDAY, "", 840, 945),
            ),
            instructor = "lhd",
            credit = 2,
            remark = "r",
            classification = "",
            department = "",
            academicYear = "",
            courseNumber = "",
            lectureNumber = "",
            category = "",
            categoryPre2025 = "",
            quota = 0,
            freshmanQuota = 0,
            registrationCount = 0,
            wasFull = false,
            reviewInfo = LectureReviewInfo(id = "", rating = null, reviewCount = 0),
        ),
    )

    // --- LectureDetail 프리뷰 ---

    val customLecture = CustomLecture(
        id = "custom1",
        courseTitle = "스터디",
        instructor = "홍길동",
        credit = 0,
        remark = "매주 월요일 저녁",
        color = LectureColor.Custom(
            foreground = 0xFFFFFFFF.toInt(),
            background = 0xFF333333.toInt(),
        ),
        lectureSessions = listOf(
            LectureSession(
                id = null,
                day = DayOfWeek.MONDAY,
                startTime = LocalTime.of(19, 0),
                endTime = LocalTime.of(20, 30),
                place = "카페",
            ),
        ),
    )

    val searchedLecture = SearchedLecture(
        id = "search1",
        courseTitle = "알고리즘",
        instructor = "이정우",
        credit = 3,
        remark = "ⓔ 컴퓨터공학부 주전공만 신청 가능",
        lectureSessions = listOf(
            LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "302-208"),
            LectureSession(null, DayOfWeek.THURSDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "302-208"),
        ),
        classification = "전선",
        department = "컴퓨터공학부",
        academicYear = "3학년",
        courseNumber = "M1522.001400",
        lectureNumber = "001",
        category = "",
        categoryPre2025 = "",
        quota = 60,
        freshmanQuota = 20,
        registrationCount = 45,
        wasFull = false,
        reviewInfo = LectureReviewInfo(id = "12345", rating = 4.2, reviewCount = 38),
    )

    val builtInColorLecture = SyllabusLecture(
        id = "builtin1",
        courseTitle = "이산수학",
        lectureSessions = listOf(
            LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "302-208"),
            LectureSession(null, DayOfWeek.THURSDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "302-208"),
        ),
        instructor = "김민수",
        credit = 3,
        remark = "",
        color = LectureColor.BuiltIn(2),
        classification = "전선",
        department = "컴퓨터공학부",
        academicYear = "2학년",
        courseNumber = "M1522.000800",
        lectureNumber = "001",
        category = "",
        categoryPre2025 = "",
        quota = 80,
        freshmanQuota = 0,
        originalLectureId = "61e4c9437d86910064ed3740",
    )

    val sampleReviewInfo = LectureReviewInfo(
        id = "review1",
        rating = 4.2,
        reviewCount = 38,
    )

    // --- LectureReminder 프리뷰 ---

    val sampleLectureReminderOptions = mapOf(
        "1" to LectureWithReminderOption("1", "컴퓨터 프로그래밍", LectureReminderOffset.NONE),
        "2" to LectureWithReminderOption("2", "이산수학", LectureReminderOffset.TEN_MINUTES_BEFORE),
        "3" to LectureWithReminderOption("3", "대학 글쓰기1", LectureReminderOffset.AT_START_TIME),
        "4" to LectureWithReminderOption("4", "통계학", LectureReminderOffset.TEN_MINUTES_AFTER),
        "5" to LectureWithReminderOption(
            "5",
            "이름긴강의이름긴강의이름긴강의이름긴강의이름긴강의이름긴강의이름긴강의이름긴강의이름긴강의이름긴강의",
            LectureReminderOffset.TEN_MINUTES_AFTER,
        ),
        "6" to LectureWithReminderOption("6", "대학 글쓰기12", LectureReminderOffset.TEN_MINUTES_AFTER),
        "7" to LectureWithReminderOption("7", "대학 글쓰기123", LectureReminderOffset.TEN_MINUTES_AFTER),
        "8" to LectureWithReminderOption("8", "이산수학2", LectureReminderOffset.TEN_MINUTES_AFTER),
        "9" to LectureWithReminderOption("9", "이산수학3", LectureReminderOffset.TEN_MINUTES_AFTER),
    )

    val sampleReminderOption = LectureWithReminderOption(
        lectureId = "1",
        lectureTitle = "논리설계",
        lectureReminderOffset = LectureReminderOffset.TEN_MINUTES_BEFORE,
    )

    val sampleReminderOptionDefault = LectureWithReminderOption.Default

    // --- 시간표 색상 분기 케이스 ---

    private fun previewSyllabusLecture(
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

    // 1~4번: BuiltIn 색 4개 강의 (두 분기 list 의 공통 부분)
    private val baseFourBuiltInLectures: List<LocalLecture> = listOf(
        previewSyllabusLecture(
            id = "1",
            courseTitle = "논리설계",
            instructor = "이창건",
            color = LectureColor.BuiltIn(0),
            sessions = listOf(
                LectureSession(null, DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(15, 15), "301-118"),
                LectureSession(null, DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(15, 15), "301-118"),
            ),
        ),
        previewSyllabusLecture(
            id = "2",
            courseTitle = "이산수학",
            instructor = "김민수",
            color = LectureColor.BuiltIn(1),
            sessions = listOf(
                LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "302-208"),
                LectureSession(null, DayOfWeek.THURSDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "302-208"),
            ),
        ),
        previewSyllabusLecture(
            id = "3",
            courseTitle = "대학 글쓰기 1",
            instructor = "박지영",
            color = LectureColor.BuiltIn(2),
            sessions = listOf(
                LectureSession(null, DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(11, 0), "5-302"),
            ),
        ),
        previewSyllabusLecture(
            id = "4",
            courseTitle = "컴퓨터 프로그래밍",
            instructor = "홍길동",
            color = LectureColor.BuiltIn(3),
            sessions = listOf(
                LectureSession(null, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 15), "302-308"),
                LectureSession(null, DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(10, 15), "302-308"),
            ),
        ),
    )

    // 모든 강의가 BuiltIn 색인 사용 (sessionless 강의 1개 포함)
    val builtInOnlyLectures: List<LocalLecture> = baseFourBuiltInLectures + listOf(
        previewSyllabusLecture(
            id = "5",
            courseTitle = "통계학",
            instructor = "최수진",
            color = LectureColor.BuiltIn(4),
            sessions = listOf(
                LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "25-210"),
            ),
        ),
        previewSyllabusLecture(
            id = "5",
            courseTitle = "통계학",
            instructor = "최수진",
            color = LectureColor.BuiltIn(4),
            sessions = listOf(),
        ),
    )

    // BuiltIn 위주이지만 커스텀 색상 강의가 하나 섞인 케이스 (실제 사용 패턴: 빌트인 테마 + 일부 강의에 사용자 커스텀 색 지정)
    val builtInWithOneCustomLecture: List<LocalLecture> = baseFourBuiltInLectures + listOf(
        previewSyllabusLecture(
            id = "5",
            courseTitle = "통계학",
            instructor = "최수진",
            color = LectureColor.Custom(foreground = 0xFFFFFFFF.toInt(), background = 0xFF6172E9.toInt()),
            sessions = listOf(
                LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "25-210"),
            ),
        ),
    )
}
