package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.domainmodel.DiaryList
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.toAbbvString
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun DiaryListRoute(
    diaryListViewModel: DiaryListViewModel = hiltViewModel(),
) {
    val courseBookDtoList = diaryListViewModel.courseBookDtoList
    val courseBookDtoIdx by diaryListViewModel._selectedCourseBookIdx.collectAsState()
    val diaryList by diaryListViewModel.diaryListUiState.collectAsState()
    DiaryListScreen(
        courseBookDtoList?.mapIndexed { idx, courseBook -> courseBook.toDataWithState(idx == courseBookDtoIdx) },
        { idx -> diaryListViewModel.clickCourseBook(idx) },
        diaryList,
    )
}

@Composable
fun DiaryListScreen(
    selectableCourseBookDto: List<Selectable<CourseBookDto>>?,
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
                        modifier = Modifier.size(30.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    )
                },
            )
            LazyRow(
                modifier = Modifier.padding(top = 20.dp, bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (selectableCourseBookDto != null) {
                    itemsIndexed(selectableCourseBookDto) { idx, (courseBook, isSelected) ->
                        Box(
                            modifier = Modifier
                                .clicks { Log.d("idx", idx.toString()); onClickCourseBook(idx) }
                                .background(if (isSelected) SNUTTColors.SNUTTTheme else SNUTTColors.LectureDiaryGray, RoundedCornerShape(50))
                                .padding(horizontal = 24.dp)
                                .height(34.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(courseBook.toAbbvString(), color = if (isSelected) SNUTTColors.White900 else SNUTTColors.EditTextLabel, style = SNUTTTypography.subtitle1.copy(fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium))
                        }
                    }
                }
            }
            when (diaryListUiState) {
                DiaryListUiState.Empty -> {}
                DiaryListUiState.Error -> {}
                DiaryListUiState.Loading -> {}
                is DiaryListUiState.Success -> {
                    LazyColumn {
                        items(diaryListUiState.diaryList.diaryList.toList()) { (date, listOfDiaryListLectureItem) ->
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
@Preview()
fun DiaryListPagePreview() {
    val courseBookDtoList = DiaryPreviewData.courseBookDtoList
    DiaryListScreen(
        selectableCourseBookDto = courseBookDtoList.mapIndexed { idx, courseBook -> courseBook.toDataWithState(idx == 0) },
        {},
        diaryListUiState = DiaryListUiState.Success(
            diaryList = DiaryList(
                courseBook = CourseBookDto(3, 24),
                diaryList = DiaryPreviewData.diaryList,
            ),
        ),
    )
}
