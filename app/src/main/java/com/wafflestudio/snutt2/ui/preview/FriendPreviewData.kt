package com.wafflestudio.snutt2.ui.preview

import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.CustomLecture
import com.wafflestudio.snutt2.domain.model.Friend
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.Nickname
import com.wafflestudio.snutt2.domain.model.Table
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.domain.model.ThemeReference
import java.time.DayOfWeek
import java.time.LocalTime

object FriendPreviewData {
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

    private val sampleFriendLecture = CustomLecture(
        id = "lecture1",
        courseTitle = "컴퓨터 프로그래밍",
        instructor = "홍길동",
        color = LectureColor.Custom(
            foreground = 0xFF3B41FF.toInt(),
            background = 0xFFCD4A2E.toInt(),
        ),
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
}
