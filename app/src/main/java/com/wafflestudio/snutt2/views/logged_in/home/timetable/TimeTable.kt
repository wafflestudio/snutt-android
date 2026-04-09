package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.compose.runtime.Composable
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.domain.model.TableTrimParam

@Composable
fun TimeTable(
    lectures: List<LocalLecture>,
    selectedLecture: SearchedLecture?,
    fittedTrimParam: TableTrimParam,
    theme: TableTheme,
    previewTheme: TableTheme? = null,
    isDarkMode: Boolean,
    compactMode: Boolean,
    tableLectureCustomOptions: TableLectureCustom,
    touchEnabled: Boolean = true,
    onLectureClick: (LocalLecture) -> Unit = {},
) {
    if (touchEnabled) {
        DrawClickEventDetector(lectures, fittedTrimParam, onLectureClick)
    }
    DrawTableGrid(fittedTrimParam)
    DrawLectures(
        lectures = lectures,
        fittedTrimParam = fittedTrimParam,
        theme = theme,
        previewTheme = previewTheme,
        isDarkMode = isDarkMode,
        compactMode = compactMode,
        tableLectureCustomOptions = tableLectureCustomOptions,
    )
    DrawSelectedLecture(selectedLecture, fittedTrimParam, compactMode, tableLectureCustomOptions)
}
