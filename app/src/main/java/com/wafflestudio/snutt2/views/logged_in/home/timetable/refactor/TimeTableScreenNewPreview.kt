package com.wafflestudio.snutt2.views.logged_in.home.timetable.refactor

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableSummary
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.domainmodel.ThemeReference
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
)

@Preview(showBackground = true, widthDp = 360, heightDp = 700, name = "Default")
@Composable
private fun TimeTableScreenDefaultPreview() {
    TimeTableScreenNew(
        uiState = baseLoadedState,
        onClickDrawerIcon = {},
        onClickTableTitle = {},
        onClickTableLecturesListIcon = {},
        onClickNotificationIcon = {},
        onClickVacancyBanner = {},
        onClickLectureCell = {},
        onDismissDialog = {},
        onConfirmChangeTableTitle = { _, _ -> },
        onBottomNavigate = {},
        onClickShareTable = {},
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 700, name = "All Badges + Banner")
@Composable
private fun TimeTableScreenAllBadgesPreview() {
    TimeTableScreenNew(
        uiState = baseLoadedState.copy(
            newSemesterExist = true,
            uncheckedNotificationExist = true,
            vacancyNotificationBannerEnabled = true,
        ),
        onClickDrawerIcon = {},
        onClickTableTitle = {},
        onClickTableLecturesListIcon = {},
        onClickNotificationIcon = {},
        onClickVacancyBanner = {},
        onClickLectureCell = {},
        onDismissDialog = {},
        onConfirmChangeTableTitle = { _, _ -> },
        onBottomNavigate = {},
        onClickShareTable = {},
    )
}

