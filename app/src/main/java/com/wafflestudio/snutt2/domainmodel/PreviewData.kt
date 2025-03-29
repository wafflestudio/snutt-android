package com.wafflestudio.snutt2.domainmodel

import androidx.compose.ui.graphics.Color
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
}
