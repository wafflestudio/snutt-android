package com.wafflestudio.snutt2.feature.home.timetable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.Table
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.domain.model.ThemeReference
import com.wafflestudio.snutt2.lib.getFittingTrimParam

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

@Preview(showBackground = true, widthDp = 360, heightDp = 700, name = "Default")
@Composable
private fun TimeTableScreenDefaultPreview() {
    TimeTableScreen(
        uiState = baseLoadedState,
        onClickDrawerIcon = {},
        onClickTableTitle = {},
        onClickTableLecturesListIcon = {},
        onClickVacancyBanner = {},
        onClickLectureCell = {},
        onDismissDialog = {},
        onConfirmChangeTableTitle = { _, _ -> },
        onClickBookmarkIcon = {},
        onClickAddBySearch = {},
        onClickAddManually = {},
        onVisitSessionlessLectureList = {},
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 700, name = "All Badges + Banner")
@Composable
private fun TimeTableScreenAllBadgesPreview() {
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
        onConfirmChangeTableTitle = { _, _ -> },
        onClickBookmarkIcon = {},
        onClickAddBySearch = {},
        onClickAddManually = {},
        onVisitSessionlessLectureList = {},
    )
}

