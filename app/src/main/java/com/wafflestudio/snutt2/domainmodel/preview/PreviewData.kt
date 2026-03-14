package com.wafflestudio.snutt2.domainmodel.preview

import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.CustomLecture
import com.wafflestudio.snutt2.domainmodel.LectureColor
import com.wafflestudio.snutt2.domainmodel.Friend
import com.wafflestudio.snutt2.domainmodel.LectureReminderOffset
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.LectureSession
import com.wafflestudio.snutt2.domainmodel.LectureWithReminderOption
import com.wafflestudio.snutt2.domainmodel.Nickname
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.domainmodel.TableSummary
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.domainmodel.ThemeReference
import com.wafflestudio.snutt2.domainmodel.domainModel
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureReviewDto
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.views.logged_in.home.friend.FriendBottomSheetContent
import com.wafflestudio.snutt2.views.logged_in.home.friend.FriendDialogState
import com.wafflestudio.snutt2.views.logged_in.home.friend.FriendDrawerTab
import com.wafflestudio.snutt2.views.logged_in.home.friend.FriendsUiState
import java.time.DayOfWeek
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

    private val sampleNotificationDtos = listOf(
        NotificationDto(
            id = "67d008df6fb110276f3ed659",
            title = "벌써 개강 2주차! 망한 시간표 공유하고 기프티콘 받아가세요",
            message = "와플스튜디오 인스타그램(@wafflestudio_official)에서 망한 시간표 대회 이벤트를 확인해보세요. (참고: [친구 > 닉네임으로 친구 추가])",
            type = 0,
            deeplink = "https://www.instagram.com/p/DG-192cTNfF",
            createdAt = "2025-03-11T09:56:47.729Z",
        ),
        NotificationDto(
            id = "67d008df6fb110276f3ed659",
            title = "벌써 개강 2주차! 망한 시간표 공유하고 기프티콘 받아가세요",
            message = "와플스튜디오 인스타그램(@wafflestudio_official)에서 망한 시간표 대회 이벤트를 확인해보세요. (참고: [친구 > 닉네임으로 친구 추가])",
            type = 1,
            deeplink = "https://www.instagram.com/p/DG-192cTNfF",
            createdAt = "2025-03-11T09:56:47.72Z",
        ),
        NotificationDto(
            id = "67d008df6fb110276f3ed659",
            title = "벌써 개강 2주차! 망한 시간표 공유하고 기프티콘 받아가세요",
            message = "와플스튜디오 인스타그램(@wafflestudio_official)에서 망한 시간표 대회 이벤트를 확인해보세요. (참고: [친구 > 닉네임으로 친구 추가])",
            type = 2,
            deeplink = "https://www.instagram.com/p/DG-192cTNfF",
            createdAt = "2025-03-11T09:56:47.7Z",
        ),
        NotificationDto(
            id = "67d008df6fb110276f3ed659",
            title = "벌써 개강 2주차! 망한 시간표 공유하고 기프티콘 받아가세요",
            message = "와플스튜디오 인스타그램(@wafflestudio_official)에서 망한 시간표 대회 이벤트를 확인해보세요. (참고: [친구 > 닉네임으로 친구 추가])",
            type = 3,
            deeplink = "https://www.instagram.com/p/DG-192cTNfF",
            createdAt = "2025-03-11T09:56:47Z",
        ),
    )

    val sampleNotifications = sampleNotificationDtos.map { it.domainModel() }

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

    private val sampleLectureDtos = listOf(
        LectureDto(
            id = "6896f09f72111e64aa138f8b",
            lecture_id = "6867c3225ef235136624b752",
            classification = "전선",
            department = "음악학과",
            academic_year = "1학년",
            course_number = "M2183.003400",
            lecture_number = "001",
            course_title = " 스튜디오 뮤직 메이킹 (로직, 거라지밴드) 입문 ",
            credit = 2,
            class_time_json = listOf(
                ClassTimeDto(
                    day = 0,
                    place = "220-413",
                    startMinute = 780,
                    endMinute = 950,
                ),
            ),
            instructor = "이지수",
            quota = 10,
            freshmanQuota = null,
            remark = "",
            category = "",
            categoryPre2025 = null,
            colorIndex = 0,
            color = ColorDto(
                fgRaw = "#FFFFFF",
                bgRaw = "#6172E9",
            ),
            registrationCount = 10,
            wasFull = false,
            review = LectureReviewDto(id = "49367"),
        ),
        LectureDto(
            id = "6896f0a272111e64aa138f8c",
            lecture_id = "6867c3225ef235136624b753",
            classification = "전선",
            department = "공과대학",
            academic_year = "석박사통합",
            course_number = "M2177.005600",
            lecture_number = "001",
            course_title = " 글로벌 공학기술 교류 특강 2 (국제 물류)",
            credit = 2,
            class_time_json = listOf(
                ClassTimeDto(
                    day = 0,
                    place = "38-B105",
                    startMinute = 630,
                    endMinute = 740,
                ),
            ),
            instructor = "박건수",
            quota = 30,
            freshmanQuota = null,
            remark = "ⓔ",
            category = "",
            categoryPre2025 = null,
            colorIndex = 0,
            color = ColorDto(
                fgRaw = "#FFFFFF",
                bgRaw = "#351C7C",
            ),
            registrationCount = 0,
            wasFull = false,
            review = LectureReviewDto(id = "43308"),
        ),
        LectureDto(
            id = "6896f0a372111e64aa138f8d",
            lecture_id = "6867c3225ef235136624b755",
            classification = "전선",
            department = "혁신공유학부",
            academic_year = "2학년",
            course_number = "M3502.016700",
            lecture_number = "001",
            course_title = "(공유)NPU 기반 인공지능 추론 및 응용",
            credit = 3,
            class_time_json = listOf(
                ClassTimeDto(
                    day = 3,
                    place = "301-B119",
                    startMinute = 720,
                    endMinute = 890,
                ),
            ),
            instructor = "윤정남",
            quota = 30,
            freshmanQuota = null,
            remark = "",
            category = "",
            categoryPre2025 = null,
            colorIndex = 0,
            color = ColorDto(
                fgRaw = "#FFFFFF",
                bgRaw = "#351C7C",
            ),
            registrationCount = 0,
            wasFull = false,
            review = LectureReviewDto(id = "59313"),
        ),
        LectureDto(
            id = "6896f0a572111e64aa138f8e",
            lecture_id = "6867c3225ef235136624b757",
            classification = "전선",
            department = "혁신공유학부",
            academic_year = "3학년",
            course_number = "M3502.001700",
            lecture_number = "002",
            course_title = "(공유)공학 지식 및 실무",
            credit = 3,
            class_time_json = listOf(
                ClassTimeDto(
                    day = 2,
                    place = "301-B119",
                    startMinute = 720,
                    endMinute = 770,
                ),
                ClassTimeDto(
                    day = 2,
                    place = "301-B119",
                    startMinute = 780,
                    endMinute = 1010,
                ),
            ),
            instructor = "이재학",
            quota = 30,
            freshmanQuota = null,
            remark = "",
            category = "",
            categoryPre2025 = null,
            colorIndex = 0,
            color = ColorDto(
                fgRaw = "#FFFFFF",
                bgRaw = "#6172E9",
            ),
            registrationCount = 0,
            wasFull = false,
            review = LectureReviewDto(id = "47436"),
        ),
        LectureDto(
            id = "6896f0a772111e64aa138f8f",
            lecture_id = "6867c3225ef235136624b759",
            classification = "전선",
            department = "혁신공유학부",
            academic_year = "4학년",
            course_number = "M3500.010700",
            lecture_number = "001",
            course_title = "(공유)에너지신산업 캡스톤디자인 2",
            credit = 3,
            class_time_json = listOf(
                ClassTimeDto(
                    day = 4,
                    place = "38-418",
                    startMinute = 600,
                    endMinute = 770,
                ),
            ),
            instructor = "김수현",
            quota = 30,
            freshmanQuota = null,
            remark = "",
            category = "",
            categoryPre2025 = null,
            colorIndex = 0,
            color = ColorDto(
                fgRaw = "#FFFFFF",
                bgRaw = "#351C7C",
            ),
            registrationCount = 0,
            wasFull = false,
            review = LectureReviewDto(id = "47797"),
        ),
        LectureDto(
            id = "6896f0bf72111e64aa138f90",
            lecture_id = null,
            classification = null,
            department = null,
            academic_year = null,
            course_number = null,
            lecture_number = null,
            course_title = "커스텀 1",
            credit = 2,
            class_time_json = listOf(
                ClassTimeDto(
                    day = 1,
                    place = "",
                    startMinute = 570,
                    endMinute = 645,
                ),
            ),
            instructor = "",
            quota = 0,
            freshmanQuota = null,
            remark = "",
            category = null,
            categoryPre2025 = null,
            colorIndex = 1,
            color = ColorDto(),
            registrationCount = 0,
            wasFull = false,
            review = null,
        ),
        LectureDto(
            id = "6896f10272111e64aa138f91",
            lecture_id = null,
            classification = null,
            department = null,
            academic_year = null,
            course_number = null,
            lecture_number = null,
            course_title = "커스텀 2",
            credit = 2,
            class_time_json = listOf(
                ClassTimeDto(
                    day = 1,
                    place = "",
                    startMinute = 690,
                    endMinute = 765,
                ),
                ClassTimeDto(
                    day = 1,
                    place = "",
                    startMinute = 840,
                    endMinute = 945,
                ),
            ),
            instructor = "lhd",
            quota = 0,
            freshmanQuota = null,
            remark = "r",
            category = null,
            categoryPre2025 = null,
            colorIndex = 0,
            color = ColorDto(
                fgRaw = "#FFFFFF",
                bgRaw = "#6172E9",
            ),
            registrationCount = 0,
            wasFull = false,
            review = null,
        ),
    )

    val sampleLectures = sampleLectureDtos.map { it.toSearchedLecture() }

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

    private val sampleFriendLecture = com.wafflestudio.snutt2.domainmodel.CustomLecture(
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
