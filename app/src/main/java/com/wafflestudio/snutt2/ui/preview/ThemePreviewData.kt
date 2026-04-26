package com.wafflestudio.snutt2.ui.preview

import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.CustomLecture
import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.domain.model.EditingTheme
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.ThemeColor
import java.time.DayOfWeek
import java.time.LocalTime

object ThemePreviewData {
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
}
