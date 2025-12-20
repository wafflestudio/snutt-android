package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.lecture_diary.DiaryRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.diary.CourseBookDiarySubmissions
import com.wafflestudio.snutt2.domainmodel.diary.DiarySummary
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import com.wafflestudio.snutt2.lib.toggle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryHistoryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DiaryHistoryUiState>(DiaryHistoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<DiaryHistoryUiEvent>(replay = 0)
    val uiEvent: SharedFlow<DiaryHistoryUiEvent> = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            diaryRepository.getMyDiarySubmissions()
                .onSuccess { courseBookDiarySubmissionsList ->
                    if (courseBookDiarySubmissionsList.isEmpty()) {
                        _uiState.value = DiaryHistoryUiState.Empty
                        return@onSuccess
                    }

                    val diarySummariesByCourseBook = convertToDiarySummariesByCourseBook(
                        courseBookDiarySubmissionsList = courseBookDiarySubmissionsList,
                    )

                    val courseBooks = courseBookDiarySubmissionsList
                        .map { it.courseBook }
                        .sorted()

                    _uiState.value = DiaryHistoryUiState.Success(
                        courseBooks = courseBooks,
                        selectedCourseBook = courseBooks[0],
                        diarySummariesByCourseBook = diarySummariesByCourseBook,
                    )
                }
                .onFailure { error ->
                    val displayMessage = displayMessageResolver.getDisplayMessage(error)
                    _uiEvent.emit(DiaryHistoryUiEvent.ShowToast(displayMessage))
                    _uiState.value = DiaryHistoryUiState.Error
                }
        }
    }

    private fun convertToDiarySummariesByCourseBook(
        courseBookDiarySubmissionsList: List<CourseBookDiarySubmissions>,
    ): Map<CourseBook, Map<LocalDate, Selectable<List<DiarySummary>>>> {
        return courseBookDiarySubmissionsList.associate { courseBookDiarySubmissions ->
            val diarySummariesByDate = courseBookDiarySubmissions.submissions
                .groupBy { submission ->
                    submission.date.toLocalDate()
                }
                .mapValues { (_, submissionList) ->
                    Selectable(
                        item = submissionList,
                        state = false,
                    )
                }
                .toSortedMap(reverseOrder())

            courseBookDiarySubmissions.courseBook to diarySummariesByDate
        }
    }

    fun selectCourseBook(coursebookIndex: Int) {
        val state = _uiState.value
        if (state !is DiaryHistoryUiState.Success) {
            return
        }

        _uiState.value = state.copy(
            selectedCourseBook = state.courseBooks[coursebookIndex],
        )
    }

    fun toggleDateExpand(date: LocalDate) {
        val state = _uiState.value
        if (state !is DiaryHistoryUiState.Success) {
            return
        }

        val selectedCourseBook = state.selectedCourseBook
        val currentDiarySummariesByDate = state.diarySummariesByCourseBook[selectedCourseBook] ?: return

        val updatedDiarySummariesByDate = currentDiarySummariesByDate + (date to currentDiarySummariesByDate[date]!!.toggle())

        _uiState.value = state.copy(
            diarySummariesByCourseBook = state.diarySummariesByCourseBook + (selectedCourseBook to updatedDiarySummariesByDate),
        )
    }

    fun deleteDiary(diaryId: String) {
        viewModelScope.launch {
            diaryRepository.removeDiarySubmission(diaryId)
                .onSuccess {
                    // UI 상태에서 삭제된 일기 제거
                    val state = _uiState.value
                    if (state !is DiaryHistoryUiState.Success) return@onSuccess

                    // 모든 CourseBook의 데이터에서 해당 일기 제거
                    val updatedDiarySummariesByCourseBook = state.diarySummariesByCourseBook
                        .mapValues { (_, diarySummariesByDate) ->
                            diarySummariesByDate
                                .mapValues { (_, selectableDiaryList) ->
                                    val filteredList = selectableDiaryList.item.filter { it.id != diaryId }
                                    selectableDiaryList.copy(item = filteredList)
                                }
                                .filterValues { it.item.isNotEmpty() } // 빈 날짜는 제거
                        }

                    _uiState.value = state.copy(
                        diarySummariesByCourseBook = updatedDiarySummariesByCourseBook,
                    )
                }
                .onFailure { error ->
                    val displayMessage = displayMessageResolver.getDisplayMessage(error)
                    _uiEvent.emit(DiaryHistoryUiEvent.ShowToast(displayMessage))
                }
        }
    }
}

sealed interface DiaryHistoryUiEvent {
    data class ShowToast(val message: String) : DiaryHistoryUiEvent
}
