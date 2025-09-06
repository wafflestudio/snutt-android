package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.DiaryList

sealed interface DiaryListUiState {
    data class Success(
        val courseBookList: List<CourseBook>,
        val selectedCourseBookIdx: Int,
        val diaryList: DiaryList,
    ) : DiaryListUiState
    data object Error : DiaryListUiState
    data object Loading : DiaryListUiState
    data object Empty : DiaryListUiState
}
