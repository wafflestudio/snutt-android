package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.domainmodel.DiaryList
import com.wafflestudio.snutt2.domainmodel.DiaryQuestionAnswer
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.toAbbvString
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import java.time.LocalDate

@Composable
fun DiaryListRoute() {
}

@Composable
fun DiaryListScreen(
    courseBookDtoList: List<CourseBookDto>,
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
                items(courseBookDtoList) { courseBook ->
                    Box(
                        modifier = Modifier
                            .background(SNUTTColors.SNUTTTheme, RoundedCornerShape(50))
                            .padding(horizontal = 24.dp)
                            .height(34.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(courseBook.toAbbvString(), color = SNUTTColors.White900, style = SNUTTTypography.subtitle1.copy(fontWeight = FontWeight.SemiBold))
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
    DiaryListScreen(
        courseBookDtoList = listOf(
            CourseBookDto(semester = 3, year = 24),
            CourseBookDto(semester = 2, year = 24),
            CourseBookDto(semester = 1, year = 24),
            CourseBookDto(semester = 4, year = 23),
            CourseBookDto(semester = 3, year = 23),
            CourseBookDto(semester = 2, year = 23),
            CourseBookDto(semester = 1, year = 23),
            CourseBookDto(semester = 4, year = 22),
        ),
        diaryListUiState = DiaryListUiState.Success(
            diaryList = DiaryList(
                courseBook = CourseBookDto(3, 24),
                diaryList = mapOf(
                    LocalDate.of(2024, 3, 20) to listOf(
                        com.wafflestudio.snutt2.domainmodel.DiaryListLectureItem(
                            lectureName = "시각디자인기초",
                            content = listOf(
                                DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                                DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                                DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                            ),
                            moreText = "좋아요",
                        ),
                        com.wafflestudio.snutt2.domainmodel.DiaryListLectureItem(
                            lectureName = "배구",
                            content = listOf(
                                DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                                DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                                DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                            ),
                            moreText = "좋아요",
                        ),

                    ),
                    LocalDate.of(2024, 3, 1) to listOf(
                        com.wafflestudio.snutt2.domainmodel.DiaryListLectureItem(
                            lectureName = "시각디자인기초",
                            content = listOf(
                                DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                                DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                                DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                            ),
                            moreText = "좋아요",
                        ),
                        com.wafflestudio.snutt2.domainmodel.DiaryListLectureItem(
                            lectureName = "배구",
                            content = listOf(
                                DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                                DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                                DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                            ),
                            moreText = "좋아요",
                        ),
                    ),
                ),
            ),
        ),
    )
}
