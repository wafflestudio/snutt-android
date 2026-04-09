package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_history

import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.diary.DiarySummary
import com.wafflestudio.snutt2.lib.Selectable
import java.time.LocalDate

sealed interface DiaryHistoryUiState {
    data class Success(
        val courseBooks: List<CourseBook>,
        val selectedCourseBook: CourseBook,
        val diarySummariesByCourseBook: DiarySummariesByCourseBook,
        val dialogState: DialogState = DialogState.None,
    ) : DiaryHistoryUiState

    sealed interface DialogState {
        data object None : DialogState
        data class DeleteDiary(
            val diary: DiarySummary,
        ) : DialogState
    }

    data object Error : DiaryHistoryUiState
    data object Loading : DiaryHistoryUiState
    data object Empty : DiaryHistoryUiState
}

// NOTE: 각 날짜 별 expand 상태가 수정 페이지 이동 후 되돌아왔을 때도 유지되기 위해, uiState 에서 관리한다.
private typealias DiarySummariesByDate = Map<LocalDate, Selectable<List<DiarySummary>>>

// NOTE: CourseBook별로 그룹화된 diary summaries
private typealias DiarySummariesByCourseBook = Map<CourseBook, DiarySummariesByDate>
