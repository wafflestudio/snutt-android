package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_history

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.diary.DiarySummariesByDate

sealed interface DiaryHistoryUiState {
    data class Success(
        val courseBooks: List<CourseBook>,
        val selectedCourseBookId: Int,
        val diarySummariesByDate: DiarySummariesByDate,
    ) : DiaryHistoryUiState

    data object Error : DiaryHistoryUiState
    data object Loading : DiaryHistoryUiState
    data object Empty : DiaryHistoryUiState
}
