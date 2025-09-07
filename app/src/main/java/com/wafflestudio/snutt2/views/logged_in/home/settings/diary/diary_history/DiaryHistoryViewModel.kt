package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.course_books.CourseBookRepository
import com.wafflestudio.snutt2.data.lecture_diary.DiaryRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.dto.core.toCourseBook
import com.wafflestudio.snutt2.lib.toggle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryHistoryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
    private val courseBookRepository: CourseBookRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DiaryHistoryUiState>(DiaryHistoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val courseBookList = courseBookRepository.getCourseBook()
                .map { courseBookDto -> courseBookDto.toCourseBook() }
            _uiState.value =
                DiaryHistoryUiState.Success(courseBookList, 0, DiaryPreviewData.diaryList)
        }
    }

    fun selectCourseBook(coursebookIndex: Int) {
        val state = _uiState.value
        if (state !is DiaryHistoryUiState.Success) {
            return
        }

        _uiState.value = state.copy(
            selectedCourseBookId = coursebookIndex,
        )
    }

    fun toggleDateExpand(date: LocalDate) {
        val state = _uiState.value
        if (state !is DiaryHistoryUiState.Success) {
            return
        }

        _uiState.value = state.copy(
            diarySummariesByDate = state.diarySummariesByDate + (date to state.diarySummariesByDate[date]!!.toggle()),
        )
    }

    fun deleteDiary(lectureId: String) {
        // TODO: 구현
    }
}
