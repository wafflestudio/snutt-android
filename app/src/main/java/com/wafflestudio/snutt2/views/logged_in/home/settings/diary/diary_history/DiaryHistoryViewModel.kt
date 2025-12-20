package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.course_books.CourseBookRepository
import com.wafflestudio.snutt2.data.lecture_diary.DiaryRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.dto.core.toDomainModel
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
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
                .map { courseBookDto -> courseBookDto.toDomainModel() }

            diaryRepository.getMyDiarySubmissions()
                .onSuccess { submissions ->
                    // TODO: submissions를 DiaryHistoryUiState.Success의 diarySummariesByDate 형식으로 변환
                    _uiState.value =
                        DiaryHistoryUiState.Success(courseBookList, 0, DiaryPreviewData.diaryList)
                }
                .onFailure { error ->
                    val displayMessage = displayMessageResolver.getDisplayMessage(error)
                    // TODO: 에러 처리
                    _uiState.value = DiaryHistoryUiState.Error
                }
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

    fun deleteDiary(diaryId: String) {
        viewModelScope.launch {
            diaryRepository.removeDiarySubmission(diaryId)
                .onSuccess {
                    // TODO: UI 상태에서 삭제된 일기 제거
                }
                .onFailure { error ->
                    val displayMessage = displayMessageResolver.getDisplayMessage(error)
                    // TODO: 에러 토스트 표시
                }
        }
    }
}
