package com.wafflestudio.snutt2.feature.home.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.BookmarkIcon
import com.wafflestudio.snutt2.ui.components.compose.DrawerIcon
import com.wafflestudio.snutt2.ui.components.compose.IconWithAlertDot
import com.wafflestudio.snutt2.ui.components.compose.TopBar
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.ui.util.SNUTTStringUtils.getCreditSumFromLectureList
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun TimeTableScreen(
    uiState: TimeTableUiState,
    onClickDrawerIcon: () -> Unit,
    onClickTableTitle: (tableSummary: TableSummary) -> Unit,
    onClickTableLecturesListIcon: () -> Unit,
    onClickVacancyBanner: () -> Unit,
    onClickLectureCell: (LocalLecture) -> Unit,
    onClickBookmarkIcon: () -> Unit,
    onClickAddBySearch: () -> Unit,
    onClickAddManually: () -> Unit,
    onVisitSessionlessLectureList: () -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmChangeTableTitle: (TableSummary, String) -> Unit,
) {
    when (uiState) {
        is TimeTableUiState.Loading -> {}
        is TimeTableUiState.Loaded -> {
            TimeTableDialogs(
                uiState = uiState,
                onDismiss = onDismissDialog,
                onConfirmChangeTableTitle = onConfirmChangeTableTitle,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SNUTTColors.White900)
                    .logImpression(AnalyticsScreen.TimetableHome),
            ) {
                TopBar(
                    title = {
                        Text(
                            text = uiState.table.summary.title,
                            style = SNUTTTypography.h2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .clicks { onClickTableTitle(uiState.table.summary) },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(
                                R.string.timetable_credit,
                                getCreditSumFromLectureList(uiState.table.lectures),
                            ),
                            style = SNUTTTypography.body2,
                            maxLines = 1,
                            color = SNUTTColors.Gray200,
                        )
                    },
                    navigationIcon = {
                        IconWithAlertDot(uiState.newSemesterExist) { centerAlignedModifier ->
                            DrawerIcon(
                                modifier = centerAlignedModifier
                                    .size(30.dp)
                                    .clicks { onClickDrawerIcon() },
                            )
                        }
                    },
                    actions = {
                        BookmarkIcon(
                            modifier = Modifier
                                .size(30.dp)
                                .clicks { onClickBookmarkIcon() },
                        )
                        TimetableMoreAction(
                            onClickAddBySearch = onClickAddBySearch,
                            onClickAddManually = onClickAddManually,
                            onClickTableLecturesListIcon = onClickTableLecturesListIcon,
                            onClickVacancyIcon = onClickVacancyBanner,
                        )
                    },
                )


                ScrollableTimetableContent(
                    modifier = Modifier.weight(1f),
                    lectures = uiState.table.lectures,
                    vacancyNotificationBannerEnabled = uiState.vacancyNotificationBannerEnabled,
                    isSessionlessLectureHintVisible = uiState.isSessionlessLectureHintVisible,
                    onVisitSessionlessLectureList = onVisitSessionlessLectureList,
                    onClickVacancyBanner = onClickVacancyBanner,
                    onClickLectureCell = onClickLectureCell,

                    fittedTrimParam = uiState.tableTrimParam,
                    theme = uiState.theme,
                    previewTheme = uiState.previewTheme,
                    compactMode = uiState.isCompactMode,
                    tableLectureCustomOptions = uiState.tableLectureCustomOptions,
                )
            }
        }
    }
}
