package com.wafflestudio.snutt2.feature.home.timetable

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.Table
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.domain.model.ThemeReference
import com.wafflestudio.snutt2.domain.model.getFittingTrimParam
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

@Preview(name = "1. Light", uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", heightDp = 1500)
@Preview(name = "2. Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", heightDp = 1500)
@Composable
private fun TimeTableScreen_Default() {
    SnuttPreviewSurface {
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
}

@Preview(name = "1. Light", uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", heightDp = 1500)
@Preview(name = "2. Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", heightDp = 1500)
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
            onConfirmChangeTableTitle = { _, _ -> },
            onClickBookmarkIcon = {},
            onClickAddBySearch = {},
            onClickAddManually = {},
            onVisitSessionlessLectureList = {},
        )
    }
}
