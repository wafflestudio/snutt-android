package com.wafflestudio.snutt2.domainmodel.preview

import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto

import androidx.compose.ui.graphics.Color
import com.wafflestudio.snutt2.domainmodel.CustomColor
import com.wafflestudio.snutt2.domainmodel.LectureSession
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.domainmodel.domainModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.LectureReminderOffset
import com.wafflestudio.snutt2.views.logged_in.home.settings.LectureWithReminderOption
import java.time.DayOfWeek
import java.time.LocalTime

object PreviewData {
    val sampleSessions = listOf(
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

    val customColor1 = CustomColor(
        foreground = Color(0xFF3B41FF),
        background = Color(0xFFCD4A2E),
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
        "5" to LectureWithReminderOption("5", "이름긴강의이름긴강의이름긴강의이름긴강의이름긴강의이름긴강의이름긴강의이름긴강의이름긴강의이름긴강의", LectureReminderOffset.TEN_MINUTES_AFTER),
        "6" to LectureWithReminderOption("6", "대학 글쓰기12", LectureReminderOffset.TEN_MINUTES_AFTER),
        "7" to LectureWithReminderOption("7", "대학 글쓰기123", LectureReminderOffset.TEN_MINUTES_AFTER),
        "8" to LectureWithReminderOption("8", "이산수학2", LectureReminderOffset.TEN_MINUTES_AFTER),
        "9" to LectureWithReminderOption("9", "이산수학3", LectureReminderOffset.TEN_MINUTES_AFTER),
    )
}
