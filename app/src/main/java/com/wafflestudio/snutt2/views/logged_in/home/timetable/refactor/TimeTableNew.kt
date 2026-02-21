package com.wafflestudio.snutt2.views.logged_in.home.timetable.refactor

import androidx.compose.runtime.Composable
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.views.logged_in.home.timetable.DrawTableGrid

@Composable
fun TimeTableNew(
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
        DrawClickEventDetectorNew(lectures, fittedTrimParam, onLectureClick)
    }
    DrawTableGrid(fittedTrimParam)
    DrawLecturesNew(
        lectures = lectures,
        fittedTrimParam = fittedTrimParam,
        theme = theme,
        previewTheme = previewTheme,
        isDarkMode = isDarkMode,
        compactMode = compactMode,
        tableLectureCustomOptions = tableLectureCustomOptions,
    )
    DrawSelectedLectureNew(selectedLecture, fittedTrimParam, compactMode, tableLectureCustomOptions)
}
