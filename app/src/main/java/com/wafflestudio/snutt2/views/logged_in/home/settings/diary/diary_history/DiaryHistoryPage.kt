package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_history

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.components.compose.ConfirmDialog
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.network.dto.core.toDomainModel
import com.wafflestudio.snutt2.lib.toAbbvString
import com.wafflestudio.snutt2.ui.SNUTTTheme
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.logged_in.home.settings.diary.DiaryTheme
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
    onDeleteDiary: (diaryId: String, courseName: String) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDeleteDiary: (diaryId: String) -> Unit,
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
                    text = "강의 일기장",
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
                                courseBook.toAbbvString(),
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
        val courseBookList =
            DiaryPreviewData.courseBookDtoList.map { courseBookDto -> courseBookDto.toDomainModel() }
        DiaryHistoryScreen(
            onNavigateBack = {},
            onClickCourseBook = {},
            onToggleExpandOfDate = {},
            onDeleteDiary = { _, _ -> },
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
            val courseBookList =
                DiaryPreviewData.courseBookDtoList.map { courseBookDto -> courseBookDto.toDomainModel() }
            DiaryHistoryScreen(
                onNavigateBack = {},
                onClickCourseBook = {},
                onToggleExpandOfDate = {},
                onDeleteDiary = { _, _ -> },
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
    onConfirmDeleteDiary: (diaryId: String) -> Unit,
) {
    when (dialogState) {
        DiaryHistoryUiState.DialogState.None -> {}
        is DiaryHistoryUiState.DialogState.DeleteDiary -> {
            ConfirmDialog(
                onDismiss = onDismiss,
                onConfirm = { onConfirmDeleteDiary(dialogState.diaryId) },
                title = "'${dialogState.courseName}'\n강의일기를 삭제하시겠습니까?",
            )
        }
    }
}
