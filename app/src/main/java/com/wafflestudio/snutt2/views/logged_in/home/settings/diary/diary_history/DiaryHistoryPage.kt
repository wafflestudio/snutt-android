package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.lib.network.dto.core.toDomainModel
import com.wafflestudio.snutt2.lib.toAbbvString
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import java.time.LocalDate

@Composable
fun DiaryHistoryRoute(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateOnboard: () -> Unit,
    onNavigateDiaryWrite: (lectureId: String) -> Unit,
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

    DiaryHistoryScreen(
        modifier = modifier,
        onNavigateBack,
        { idx -> viewModel.selectCourseBook(idx) },
        viewModel::toggleDateExpand,
        onNavigateDiaryWrite,
        viewModel::deleteDiary,
        uiState,
    )
}

@Composable
fun DiaryHistoryScreen(
    modifier: Modifier,
    onNavigateBack: () -> Unit,
    onClickCourseBook: (coursebookIndex: Int) -> Unit,
    onToggleExpandOfDate: (date: LocalDate) -> Unit,
    onEditDiary: (lectureId: String) -> Unit,
    onDeleteDiary: (lectureId: String) -> Unit,
    diaryHistoryUiState: DiaryHistoryUiState,
) {
    Column(modifier = modifier.background(SNUTTColors.White900)) {
        TopBar(
            title = { Text("강의 일기장") },
            navigationIcon = {
                ArrowBackIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { onNavigateBack() },
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            },
        )

        when (diaryHistoryUiState) {
            DiaryHistoryUiState.Empty -> {}
            DiaryHistoryUiState.Error -> {}
            DiaryHistoryUiState.Loading -> {}
            is DiaryHistoryUiState.Success -> {
                LazyRow(
                    modifier = Modifier.padding(top = 20.dp, bottom = 12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(diaryHistoryUiState.courseBooks) { idx, courseBook ->
                        val isSelected = courseBook == diaryHistoryUiState.selectedCourseBook

                        Box(
                            modifier = Modifier
                                .clicks { onClickCourseBook(idx) }
                                .background(
                                    if (isSelected) SNUTTColors.SNUTTTheme else SNUTTColors.LectureDiaryGray,
                                    RoundedCornerShape(50),
                                )
                                .padding(horizontal = 24.dp)
                                .height(34.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                courseBook.toAbbvString(),
                                color = if (isSelected) SNUTTColors.White900 else SNUTTColors.EditTextLabel,
                                style = SNUTTTypography.subtitle1.copy(fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium),
                            )
                        }
                    }
                }
                LazyColumn {
                    val diarySummariesByDate = diaryHistoryUiState.diarySummariesByCourseBook[diaryHistoryUiState.selectedCourseBook] ?: emptyMap()
                    items(diarySummariesByDate.toList()) { (date, listOfDiaryListLectureItemWithExpandState) ->
                        val (listOfDiaryListLectureItem, expanded) = listOfDiaryListLectureItemWithExpandState

                        DiarySummariesOfDay(
                            date = date,
                            listOfDiaryListLectureItem,
                            expanded,
                            toggleExpended = {
                                onToggleExpandOfDate(date)
                            },
                            onEditDiary = onEditDiary,
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
    val courseBookList =
        DiaryPreviewData.courseBookDtoList.map { courseBookDto -> courseBookDto.toDomainModel() }
    DiaryHistoryScreen(
        modifier = Modifier,
        onNavigateBack = {},
        {},
        {},
        {},
        {},
        diaryHistoryUiState = DiaryHistoryUiState.Success(
            courseBooks = courseBookList,
            selectedCourseBook = courseBookList[0],
            diarySummariesByCourseBook = mapOf(courseBookList[0] to DiaryPreviewData.diaryList),
        ),
    )
}
