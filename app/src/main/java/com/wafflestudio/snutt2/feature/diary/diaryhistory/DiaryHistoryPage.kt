package com.wafflestudio.snutt2.feature.diary.diaryhistory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.diary.DiarySummary
import com.wafflestudio.snutt2.feature.diary.DiaryTheme
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.TopBar
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.DiaryPreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.formatter.toAbbvString
import com.wafflestudio.snutt2.ui.util.toast
import java.time.LocalDate

@Composable
fun DiaryHistoryRoute(
    onNavigateBack: () -> Unit,
    onNavigateToDiaryWrite: (lectureId: String, courseTitle: String) -> Unit,
    viewModel: DiaryHistoryViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is DiaryHistoryUiEvent.ShowToast -> {
                    val message = uiEvent.message
                    if (message.isNotEmpty()) {
                        context.toast(message)
                    }
                }
                is DiaryHistoryUiEvent.NavigateToDiaryWrite -> {
                    onNavigateToDiaryWrite(uiEvent.lectureId, uiEvent.courseTitle)
                }
            }
        }
    }

    DiaryTheme {
        DiaryHistoryScreen(
            uiState = uiState,
            onNavigateBack = onNavigateBack,
            onClickCourseBook = { idx -> viewModel.selectCourseBook(idx) },
            onToggleExpandOfDate = viewModel::toggleDateExpand,
            onDeleteDiary = viewModel::openDeleteDiaryDialog,
            onDismissDialog = viewModel::dismissDialog,
            onConfirmDeleteDiary = viewModel::confirmDeleteDiary,
            onClickWriteFromEmpty = viewModel::requestDiaryWrite,
            onDismissWriteUnavailableDialog = viewModel::dismissWriteUnavailableDialog,
        )
    }
}

@Composable
fun DiaryHistoryScreen(
    uiState: DiaryHistoryUiState,
    onNavigateBack: () -> Unit,
    onClickCourseBook: (coursebookIndex: Int) -> Unit,
    onToggleExpandOfDate: (date: LocalDate) -> Unit,
    onDeleteDiary: (DiarySummary) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDeleteDiary: (DiarySummary) -> Unit,
    onClickWriteFromEmpty: () -> Unit,
    onDismissWriteUnavailableDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DiaryHistoryDialogs(
        uiState = uiState,
        onDismissDialog = onDismissDialog,
        onConfirmDeleteDiary = onConfirmDeleteDiary,
        onDismissWriteUnavailableDialog = onDismissWriteUnavailableDialog,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DiaryTheme.colors.screenBackground),
    ) {
        TopBar(
            title = {
                Text(
                    text = stringResource(R.string.diary_app_bar_title),
                    style = SNUTTTypography.h3,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            navigationIcon = {
                SnuttIcon(
                    R.drawable.ic_arrow_back,
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { onNavigateBack() },
                    colorFilter = ColorFilter.tint(DiaryTheme.colors.iconPrimary),
                )
            },
        )

        when (uiState) {
            is DiaryHistoryUiState.Empty -> DiaryHistoryEmpty(
                onClickAction = onClickWriteFromEmpty,
            )
            DiaryHistoryUiState.Error -> {}
            DiaryHistoryUiState.Loading -> {}
            is DiaryHistoryUiState.Success -> {
                LazyRow(
                    modifier = Modifier.padding(top = 20.dp, bottom = 12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(uiState.courseBooks) { idx, courseBook ->
                        val isSelected = courseBook == uiState.selectedCourseBook

                        Box(
                            modifier = Modifier
                                .clicks { onClickCourseBook(idx) }
                                .background(
                                    if (isSelected) DiaryTheme.colors.filterPillSelectedBackground else DiaryTheme.colors.filterPillUnselectedBackground,
                                    RoundedCornerShape(corner = CornerSize(17.dp)),
                                )
                                .padding(horizontal = 24.dp)
                                .height(34.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                courseBook.toAbbvString(LocalContext.current),
                                color = if (isSelected) DiaryTheme.colors.filterPillSelectedText else DiaryTheme.colors.filterPillUnselectedText,
                                style = SNUTTTypography.subtitle1.copy(fontSize = 15.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal),
                            )
                        }
                    }
                }
                LazyColumn {
                    val diarySummariesByDate = uiState.diarySummariesByCourseBook[uiState.selectedCourseBook] ?: emptyMap()
                    items(diarySummariesByDate.toList()) { (date, listOfDiaryListLectureItemWithExpandState) ->
                        val (listOfDiaryListLectureItem, expanded) = listOfDiaryListLectureItemWithExpandState

                        DiarySummariesOfDay(
                            date = date,
                            listOfDiaryListLectureItem,
                            expanded,
                            toggleExpended = {
                                onToggleExpandOfDate(date)
                            },
                            onDeleteDiary = onDeleteDiary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiaryHistoryEmpty(
    onClickAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DiaryTheme.colors.screenBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SnuttIcon(
                id = R.drawable.ic_cat_retry,
                modifier = Modifier.size(60.dp),
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.diary_history_empty_title),
                style = SNUTTTypography.h3.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.diary_history_empty_subtitle),
                style = SNUTTTypography.body1.copy(
                    fontSize = 13.sp,
                    color = DiaryTheme.colors.textSubtitle,
                ),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color(0xffe4e4e5),
                        shape = RoundedCornerShape(30.dp),
                    )
                    .clicks { onClickAction() }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.diary_history_empty_action),
                    style = SNUTTTypography.body1.copy(
                        fontSize = 15.sp,
                        color = SNUTTColors.Black900,
                    ),
                )
                Spacer(modifier = Modifier.width(4.dp))
                SnuttIcon(
                    id = R.drawable.ic_arrow_right,
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(DiaryTheme.colors.iconPrimary),
                )
            }
        }
    }
}

@SnuttPreview
@Composable
private fun DiaryHistoryScreen_Default() {
    SnuttPreviewSurface {
        DiaryTheme {
            val courseBookList = DiaryPreviewData.courseBookList
            DiaryHistoryScreen(
                onNavigateBack = {},
                onClickCourseBook = {},
                onToggleExpandOfDate = {},
                onDeleteDiary = { _ -> },
                onDismissDialog = {},
                onConfirmDeleteDiary = {},
                onClickWriteFromEmpty = {},
                onDismissWriteUnavailableDialog = {},
                uiState = DiaryHistoryUiState.Success(
                    courseBooks = courseBookList,
                    selectedCourseBook = courseBookList[0],
                    diarySummariesByCourseBook = mapOf(courseBookList[0] to DiaryPreviewData.diaryList),
                ),
            )
        }
    }
}

@SnuttPreview
@Composable
private fun DiaryHistoryScreen_Empty() {
    SnuttPreviewSurface {
        DiaryTheme {
            DiaryHistoryScreen(
                onNavigateBack = {},
                onClickCourseBook = {},
                onToggleExpandOfDate = {},
                onDeleteDiary = { _ -> },
                onDismissDialog = {},
                onConfirmDeleteDiary = {},
                onClickWriteFromEmpty = {},
                onDismissWriteUnavailableDialog = {},
                uiState = DiaryHistoryUiState.Empty(),
            )
        }
    }
}
