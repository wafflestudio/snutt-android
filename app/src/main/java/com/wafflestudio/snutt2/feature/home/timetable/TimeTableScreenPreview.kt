package com.wafflestudio.snutt2.feature.home.timetable

import androidx.compose.runtime.Composable
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.Table
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.domain.model.ThemeReference
import com.wafflestudio.snutt2.domain.model.getFittingTrimParam
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface

private val sampleTable = Table(
    summary = TableSummary.Default,
    lectures = builtInOnlyLectures,
    themeRef = ThemeReference.BuiltIn(0),
)

private val baseLoadedState = TimeTableUiState.Loaded(
    table = sampleTable,
    theme = BuiltInTheme.SNUTT,
    previewTheme = null,
    tableTrimParam = builtInOnlyLectures.getFittingTrimParam(TableTrimParam.Default),
    isCompactMode = false,
    tableLectureCustomOptions = TableLectureCustom.Default,
    newSemesterExist = false,
    uncheckedNotificationExist = false,
    vacancyNotificationBannerEnabled = false,
    dialogState = TimeTableUiState.DialogState.None,
    isSessionlessLectureHintVisible = true,
)

@SnuttPreview
@Composable
private fun TimeTableScreen_AllBadgesAndBanner() {
    SnuttPreviewSurface {
        TimeTableScreen(
            uiState = baseLoadedState.copy(
                newSemesterExist = true,
                uncheckedNotificationExist = true,
                vacancyNotificationBannerEnabled = true,
            ),
            onClickDrawerIcon = {},
            onClickTableTitle = {},
            onClickTableLecturesListIcon = {},
            onClickVacancyBanner = {},
            onClickLectureCell = {},
            onDismissDialog = {},
            onChangeTableNameTitleChange = {},
            onConfirmChangeTableTitle = {},
            onClickBookmarkIcon = {},
            onClickAddBySearch = {},
            onClickAddManually = {},
            onVisitSessionlessLectureList = {},
        )
    }
}
