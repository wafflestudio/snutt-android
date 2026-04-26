package com.wafflestudio.snutt2.domain.model.preview

import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.CustomLecture
import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.domain.model.EditingTheme
import com.wafflestudio.snutt2.domain.model.Friend
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset
import com.wafflestudio.snutt2.domain.model.LectureReviewInfo
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.Nickname
import com.wafflestudio.snutt2.domain.model.Notification
import com.wafflestudio.snutt2.domain.model.NotificationType
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.SyllabusLecture
import com.wafflestudio.snutt2.domain.model.Table
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.domain.model.ThemeColor
import com.wafflestudio.snutt2.domain.model.ThemeReference
import com.wafflestudio.snutt2.feature.friend.FriendBottomSheetContent
import com.wafflestudio.snutt2.feature.friend.FriendDialogState
import com.wafflestudio.snutt2.feature.friend.FriendDrawerTab
import com.wafflestudio.snutt2.feature.friend.FriendsUiState
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

object PreviewData {
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

    private val customColor1 = LectureColor.Custom(
        foreground = 0xFF3B41FF.toInt(),
        background = 0xFFCD4A2E.toInt(),
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

    private val sampleNotificationTitle = "벌써 개강 2주차! 망한 시간표 공유하고 기프티콘 받아가세요"
    private val sampleNotificationMessage =
        "와플스튜디오 인스타그램(@wafflestudio_official)에서 망한 시간표 대회 이벤트를 확인해보세요. (참고: [친구 > 닉네임으로 친구 추가])"
    private val sampleNotificationDeeplink = "https://www.instagram.com/p/DG-192cTNfF"
    private val sampleNotificationCreatedAt = LocalDateTime.of(2025, 3, 11, 18, 56, 47)

    val sampleNotifications: List<Notification> = listOf(
        NotificationType.Warning,
        NotificationType.Calendar,
        NotificationType.RefreshTime,
        NotificationType.Trash,
    ).map { type ->
        Notification(
            title = sampleNotificationTitle,
            message = sampleNotificationMessage,
            createdAt = sampleNotificationCreatedAt,
            type = type,
            deeplink = sampleNotificationDeeplink,
        )
    }

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

    val sampleFriends = listOf(
        Friend(
            id = "friend1",
            userId = "user1",
            displayName = "김철수",
            nickname = Nickname(nickname = "cheolsu", tag = "1234"),
            createdAt = "2025-01-15T10:30:00Z",
        ),
        Friend(
            id = "friend2",
            userId = "user2",
            displayName = null,
            nickname = Nickname(nickname = "younghee", tag = "5678"),
            createdAt = "2025-02-20T14:20:00Z",
        ),
        Friend(
            id = "friend3",
            userId = "user3",
            displayName = "박민수",
            nickname = Nickname(nickname = "minsu_park", tag = "9012"),
            createdAt = "2025-03-05T09:15:00Z",
        ),
    )

    val sampleRequestedFriends = listOf(
        Friend(
            id = "requested1",
            userId = "user4",
            displayName = null,
            nickname = Nickname(nickname = "jiwon", tag = "3456"),
            createdAt = "2025-03-10T16:45:00Z",
        ),
        Friend(
            id = "requested2",
            userId = "user5",
            displayName = null,
            nickname = Nickname(nickname = "sungmin", tag = "7890"),
            createdAt = "2025-03-11T11:30:00Z",
        ),
    )

    val sampleCourseBooks = listOf(
        CourseBook(semester = 1, year = 2025),
        CourseBook(semester = 2, year = 2024),
        CourseBook(semester = 1, year = 2024),
    )

    // --- HomeDrawer 프리뷰 ---

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

    private val sampleFriendLecture = com.wafflestudio.snutt2.domain.model.CustomLecture(
        id = "lecture1",
        courseTitle = "컴퓨터 프로그래밍",
        instructor = "홍길동",
        color = customColor1,
        lectureSessions = listOf(
            LectureSession(
                id = null,
                day = DayOfWeek.MONDAY,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(10, 30),
                place = "302-308",
            ),
            LectureSession(
                id = null,
                day = DayOfWeek.WEDNESDAY,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(10, 30),
                place = "302-308",
            ),
        ),
        credit = 3,
        remark = "",
    )

    val sampleFriendTable = Table(
        summary = TableSummary(
            id = "friend_table1",
            courseBook = CourseBook(semester = 1, year = 2025),
            title = "2025-1학기",
            totalCredit = 18,
            isPrimary = true,
        ),
        lectures = listOf(sampleFriendLecture),
        themeRef = ThemeReference.BuiltIn(0),
    )

    // --- Theme 프리뷰 ---

    val previewCustomTheme1 = CustomTheme(
        id = "p1",
        name = "봄 테마",
        isFromMarket = false,
        colors = listOf(
            ThemeColor(0xFFFFFFFF.toInt(), 0xFFFF6B6B.toInt()),
            ThemeColor(0xFFFFFFFF.toInt(), 0xFFFF8E53.toInt()),
            ThemeColor(0xFF000000.toInt(), 0xFFFFD93D.toInt()),
            ThemeColor(0xFFFFFFFF.toInt(), 0xFF6BCB77.toInt()),
            ThemeColor(0xFFFFFFFF.toInt(), 0xFF4D96FF.toInt()),
        ),
    )

    val previewCustomTheme2 = CustomTheme(
        id = "p2",
        name = "오션 테마",
        isFromMarket = false,
        colors = listOf(
            ThemeColor(0xFFFFFFFF.toInt(), 0xFF0077B6.toInt()),
            ThemeColor(0xFFFFFFFF.toInt(), 0xFF0096C7.toInt()),
            ThemeColor(0xFFFFFFFF.toInt(), 0xFF00B4D8.toInt()),
            ThemeColor(0xFF000000.toInt(), 0xFF90E0EF.toInt()),
            ThemeColor(0xFF000000.toInt(), 0xFFCAF0F8.toInt()),
        ),
    )

    val previewMarketTheme = CustomTheme(
        id = "mkt",
        name = "갤럭시 테마",
        isFromMarket = true,
        colors = listOf(
            ThemeColor(0xFFFFFFFF.toInt(), 0xFF845EC2.toInt()),
            ThemeColor(0xFFFFFFFF.toInt(), 0xFFD65DB1.toInt()),
            ThemeColor(0xFFFFFFFF.toInt(), 0xFFFF6F91.toInt()),
            ThemeColor(0xFFFFFFFF.toInt(), 0xFFFF9671.toInt()),
            ThemeColor(0xFF000000.toInt(), 0xFFFFC75F.toInt()),
        ),
    )

    // --- ThemeDetail 프리뷰 ---

    val previewEditingThemeCustom = EditingTheme.fromTableTheme(previewCustomTheme1)
    val previewEditingThemeMarket = EditingTheme.fromTableTheme(previewMarketTheme)
    val previewEditingThemeBuiltIn = EditingTheme.fromTableTheme(BuiltInTheme.SNUTT)

    val themeDetailSampleLectures: List<LocalLecture> = listOf(
        CustomLecture(
            id = "theme-preview-1",
            courseTitle = "컴퓨터 프로그래밍",
            instructor = "이창건",
            credit = 3,
            remark = "",
            color = LectureColor.BuiltIn(0),
            lectureSessions = listOf(
                LectureSession(null, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 30), "302-308"),
                LectureSession(null, DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(10, 30), "302-308"),
            ),
        ),
        CustomLecture(
            id = "theme-preview-2",
            courseTitle = "이산수학",
            instructor = "김민수",
            credit = 3,
            remark = "",
            color = LectureColor.BuiltIn(1),
            lectureSessions = listOf(
                LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "302-208"),
                LectureSession(null, DayOfWeek.THURSDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "302-208"),
            ),
        ),
        CustomLecture(
            id = "theme-preview-3",
            courseTitle = "선형대수학",
            instructor = "박지원",
            credit = 3,
            remark = "",
            color = LectureColor.BuiltIn(2),
            lectureSessions = listOf(
                LectureSession(null, DayOfWeek.MONDAY, LocalTime.of(13, 0), LocalTime.of(14, 30), "27-220"),
                LectureSession(null, DayOfWeek.WEDNESDAY, LocalTime.of(13, 0), LocalTime.of(14, 30), "27-220"),
            ),
        ),
        CustomLecture(
            id = "theme-preview-4",
            courseTitle = "대학 글쓰기",
            instructor = "최영민",
            credit = 3,
            remark = "",
            color = LectureColor.BuiltIn(3),
            lectureSessions = listOf(
                LectureSession(null, DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(12, 30), "3-101"),
            ),
        ),
        CustomLecture(
            id = "theme-preview-5",
            courseTitle = "통계학",
            instructor = "윤정남",
            credit = 3,
            remark = "",
            color = LectureColor.BuiltIn(4),
            lectureSessions = listOf(
                LectureSession(null, DayOfWeek.TUESDAY, LocalTime.of(15, 0), LocalTime.of(16, 30), "25-103"),
                LectureSession(null, DayOfWeek.THURSDAY, LocalTime.of(15, 0), LocalTime.of(16, 30), "25-103"),
            ),
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

    val sampleReviewInfo = LectureReviewInfo(
        id = "review1",
        rating = 4.2,
        reviewCount = 38,
    )

    val sampleReminderOption = LectureWithReminderOption(
        lectureId = "1",
        lectureTitle = "논리설계",
        lectureReminderOffset = LectureReminderOffset.TEN_MINUTES_BEFORE,
    )

    val sampleReminderOptionDefault = LectureWithReminderOption.Default

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

    val emptyReminderOption = LectureWithReminderOption(
        lectureId = "",
        lectureTitle = "",
        lectureReminderOffset = LectureReminderOffset.NONE,
    )

    val sampleFriendsUiState = FriendsUiState.Loaded(
        activeFriends = sampleFriends,
        requestedFriends = sampleRequestedFriends,
        selectedFriend = sampleFriends.firstOrNull(),
        selectedFriendCourseBooks = sampleCourseBooks,
        selectedCourseBook = sampleCourseBooks.firstOrNull(),
        selectedFriendTable = sampleFriendTable,
        selectedFriendTableTheme = BuiltInTheme.SNUTT,
        selectedFriendTableTrimParam = TableTrimParam.Default,
        drawerTab = FriendDrawerTab.ACTIVE,
        bottomSheetContent = FriendBottomSheetContent.Hidden,
        dialogState = FriendDialogState.None,
    )
}
