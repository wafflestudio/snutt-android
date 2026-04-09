package com.wafflestudio.snutt2.feature.diary.diary_history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.ui.components.compose.ConfirmDialog
import com.wafflestudio.snutt2.ui.components.compose.TopBar
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.domain.model.diary.DiarySummary
import com.wafflestudio.snutt2.domain.model.preview.DiaryPreviewData
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.toAbbvString
import com.wafflestudio.snutt2.ui.theme.SNUTTTheme
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.feature.diary.DiaryTheme
import java.time.LocalDate

@Composable
fun DiaryHistoryRoute(
    onNavigateBack: () -> Unit,
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
            }
        }
    }

    DiaryTheme {
        DiaryHistoryScreen(
            uiState,
            onNavigateBack,
            { idx -> viewModel.selectCourseBook(idx) },
            viewModel::toggleDateExpand,
            viewModel::openDeleteDiaryDialog,
            viewModel::dismissDialog,
            viewModel::confirmDeleteDiary,
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
    modifier: Modifier = Modifier,
) {
    if (uiState is DiaryHistoryUiState.Success) {
        DiaryHistoryDialogs(
            dialogState = uiState.dialogState,
            onDismiss = onDismissDialog,
            onConfirmDeleteDiary = onConfirmDeleteDiary,
        )
    }

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
                ArrowBackIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { onNavigateBack() },
                    colorFilter = ColorFilter.tint(DiaryTheme.colors.iconPrimary),
                )
            },
        )

        when (uiState) {
            DiaryHistoryUiState.Empty -> {}
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
@Preview
fun DiaryListPagePreview() {
    DiaryTheme {
        val courseBookList = DiaryPreviewData.courseBookList
        DiaryHistoryScreen(
            onNavigateBack = {},
            onClickCourseBook = {},
            onToggleExpandOfDate = {},
            onDeleteDiary = { _ -> },
            onDismissDialog = {},
            onConfirmDeleteDiary = {},
            uiState = DiaryHistoryUiState.Success(
                courseBooks = courseBookList,
                selectedCourseBook = courseBookList[0],
                diarySummariesByCourseBook = mapOf(courseBookList[0] to DiaryPreviewData.diaryList),
            ),
        )
    }
}

@Composable
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, heightDp = 1500)
fun DiaryListPageDarkPreview() {
    SNUTTTheme {
        DiaryTheme(darkTheme = true) {
            val courseBookList = DiaryPreviewData.courseBookList
            DiaryHistoryScreen(
                onNavigateBack = {},
                onClickCourseBook = {},
                onToggleExpandOfDate = {},
                onDeleteDiary = { _ -> },
                onDismissDialog = {},
                onConfirmDeleteDiary = {},
                uiState = DiaryHistoryUiState.Success(
                    courseBooks = courseBookList,
                    selectedCourseBook = courseBookList[0],
                    diarySummariesByCourseBook = mapOf(courseBookList[0] to DiaryPreviewData.diaryList),
                ),
            )
        }
    }
}

@Composable
private fun DiaryHistoryDialogs(
    dialogState: DiaryHistoryUiState.DialogState,
    onDismiss: () -> Unit,
    onConfirmDeleteDiary: (DiarySummary) -> Unit,
) {
    when (dialogState) {
        DiaryHistoryUiState.DialogState.None -> {}
        is DiaryHistoryUiState.DialogState.DeleteDiary -> {
            ConfirmDialog(
                onDismiss = onDismiss,
                onConfirm = { onConfirmDeleteDiary(dialogState.diary) },
                title = stringResource(R.string.diary_delete_confirm_message, dialogState.diary.courseName),
            )
        }
    }
}
