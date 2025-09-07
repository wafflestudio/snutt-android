package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_history

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.diary.DiaryList

sealed interface DiaryHistoryUiState {
    data class Success(
        val courseBookList: List<CourseBook>,
        val selectedCourseBookIdx: Int,
        val diaryList: DiaryList,
    ) : DiaryHistoryUiState

    data object Error : DiaryHistoryUiState
    data object Loading : DiaryHistoryUiState
    data object Empty : DiaryHistoryUiState
}
