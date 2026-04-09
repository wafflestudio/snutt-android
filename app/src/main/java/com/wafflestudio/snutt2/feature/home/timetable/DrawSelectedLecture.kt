package com.wafflestudio.snutt2.feature.home.timetable

import androidx.compose.runtime.Composable
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableTrimParam

private const val SELECTED_FG_COLOR = -0xcccccd  // 0xFF333333
private const val SELECTED_BG_COLOR = -0x1f1f20  // 0xFFE0E0E0

@Composable
fun DrawSelectedLecture(
    selectedLecture: SearchedLecture?,
    fittedTrimParam: TableTrimParam,
    compactMode: Boolean,
    tableLectureCustomOptions: TableLectureCustom,
) {
    selectedLecture?.run {
        for (session in lectureSessions) {
            DrawClassTime(
                session = session,
                foregroundColor = SELECTED_FG_COLOR,
                backgroundColor = SELECTED_BG_COLOR,
                courseTitle = courseTitle,
                lectureNumber = lectureNumber,
                instructorName = instructor,
                isCustom = false,
                fittedTrimParam = fittedTrimParam,
                compactMode = compactMode,
                tableLectureCustomOptions = tableLectureCustomOptions,
            )
        }
    }
}
