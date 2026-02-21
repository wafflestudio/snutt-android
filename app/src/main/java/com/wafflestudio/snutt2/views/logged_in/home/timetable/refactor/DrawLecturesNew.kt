package com.wafflestudio.snutt2.views.logged_in.home.timetable.refactor

import androidx.compose.runtime.Composable
import com.wafflestudio.snutt2.domainmodel.CustomLecture
import com.wafflestudio.snutt2.domainmodel.LectureColor
import com.wafflestudio.snutt2.domainmodel.LectureSyllabusInfo
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.lib.trimByTrimParam

@Composable
fun DrawLecturesNew(
    lectures: List<LocalLecture>,
    fittedTrimParam: TableTrimParam,
    theme: TableTheme,
    isDarkMode: Boolean,
    compactMode: Boolean,
    tableLectureCustomOptions: TableLectureCustom,
) {
    lectures.forEach { lecture ->
        val (fg, bg) = when (val color = lecture.color) {
            is LectureColor.Custom -> color.foreground to color.background
            is LectureColor.BuiltIn -> {
                val palette = theme.getColors(isDarkMode)
                val entry = palette[(color.colorIndex.toInt() - 1).coerceIn(palette.indices)]
                entry.foreground to entry.background
            }
        }

        val lectureNumber = if (lecture is LectureSyllabusInfo) lecture.lectureNumber else ""
        val isCustom = lecture is CustomLecture

        lecture.lectureSessions
            .mapNotNull { it.trimByTrimParam(fittedTrimParam) }
            .forEach { session ->
                DrawClassTimeNew(
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
