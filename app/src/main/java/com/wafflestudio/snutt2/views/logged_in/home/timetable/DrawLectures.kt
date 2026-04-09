package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.compose.runtime.Composable
import com.wafflestudio.snutt2.domain.model.CustomLecture
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.domain.model.LectureSyllabusInfo
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.lib.trimByTrimParam

@Composable
fun DrawLectures(
    lectures: List<LocalLecture>,
    fittedTrimParam: TableTrimParam,
    theme: TableTheme,
    previewTheme: TableTheme? = null,
    isDarkMode: Boolean,
    compactMode: Boolean,
    tableLectureCustomOptions: TableLectureCustom,
) {
    lectures.forEachIndexed { index, lecture ->
        val (fg, bg) = if (previewTheme != null) {
            val palette = previewTheme.getColors(isDarkMode)
            val entry = palette[index % palette.size]

            entry.foreground to entry.background
        } else {
            when (val color = lecture.color) {
                is LectureColor.Custom -> color.foreground to color.background
                is LectureColor.BuiltIn -> {
                    val palette = theme.getColors(isDarkMode)
                    val entry = palette[color.colorIndex.coerceIn(palette.indices)]
                    entry.foreground to entry.background
                }
            }
        }

        val lectureNumber = if (lecture is LectureSyllabusInfo) lecture.lectureNumber else ""
        val isCustom = lecture is CustomLecture

        lecture.lectureSessions
            .mapNotNull { it.trimByTrimParam(fittedTrimParam) }
            .forEach { session ->
                DrawClassTime(
                    session = session,
                    foregroundColor = fg,
                    backgroundColor = bg,
                    courseTitle = lecture.courseTitle,
                    lectureNumber = lectureNumber,
                    instructorName = lecture.instructor,
                    isCustom = isCustom,
                    fittedTrimParam = fittedTrimParam,
                    compactMode = compactMode,
                    tableLectureCustomOptions = tableLectureCustomOptions,
                )
            }
    }
}
