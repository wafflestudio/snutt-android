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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.lib.network.dto.core.toCourseBook
import com.wafflestudio.snutt2.lib.toAbbvString
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun DiaryListRoute(
    modifier: Modifier = Modifier,
    diaryListViewModel: DiaryListViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateOnboard: () -> Unit,
    onNavigateDiaryWrite: (String) -> Unit,
) {
    val diaryList by diaryListViewModel.diaryListUiState.collectAsState()
    DiaryListScreen(
        onNavigateBack,
        { idx -> diaryListViewModel.clickCourseBook(idx) },
        diaryList,
    )
}

@Composable
fun DiaryListScreen(
    onNavigateBack: () -> Unit,
    onClickCourseBook: (Int) -> Unit,
    diaryListUiState: DiaryListUiState,
) {
    Box {
        Column(
            modifier = Modifier.background(SNUTTColors.White900),
        ) {
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

            when (diaryListUiState) {
                DiaryListUiState.Empty -> {}
                DiaryListUiState.Error -> {}
                DiaryListUiState.Loading -> {}
                is DiaryListUiState.Success -> {
                    LazyRow(
                        modifier = Modifier.padding(top = 20.dp, bottom = 12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        itemsIndexed(diaryListUiState.courseBookList) { idx, courseBook ->
                            val isSelected = idx == diaryListUiState.selectedCourseBookIdx
                            Box(
                                modifier = Modifier
                                    .clicks { onClickCourseBook(idx) }
                                    .background(
                                        if (isSelected) SNUTTColors.SNUTTTheme else SNUTTColors.LectureDiaryGray,
                                        RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 24.dp)
                                    .height(34.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    courseBook.toAbbvString(),
                                    color = if (isSelected) SNUTTColors.White900 else SNUTTColors.EditTextLabel,
                                    style = SNUTTTypography.subtitle1.copy(fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium)
                                )
                            }
                        }
                    }
                    LazyColumn {
                        items(diaryListUiState.diaryList.toList()) { (date, listOfDiaryListLectureItem) ->
                            DiaryListDateItem(
                                date = date,
                                listOfDiaryListLectureItem,
                            )
                        }
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
        DiaryPreviewData.courseBookDtoList.map { courseBookDto -> courseBookDto.toCourseBook() }
    DiaryListScreen(
        onNavigateBack = {},
        {},
        diaryListUiState = DiaryListUiState.Success(
            courseBookList = courseBookList,
            selectedCourseBookIdx = 0,
            diaryList = DiaryPreviewData.diaryList,
        ),
    )
}
