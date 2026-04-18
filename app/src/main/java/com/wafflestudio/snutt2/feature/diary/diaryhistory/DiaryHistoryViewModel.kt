package com.wafflestudio.snutt2.feature.diary.diaryhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.lecturediary.DiaryRepository
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.diary.CourseBookDiarySubmissions
import com.wafflestudio.snutt2.domain.model.diary.DiarySummary
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.toggle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
                        _uiState.update { DiaryHistoryUiState.Empty }
                        return@onSuccess
                    }

                    val diarySummariesByCourseBook = convertToDiarySummariesByCourseBook(
                        courseBookDiarySubmissionsList = courseBookDiarySubmissionsList,
                    )

                    val courseBooks = courseBookDiarySubmissionsList
                        .map { it.courseBook }
                        .sorted()

                    _uiState.update {
                        DiaryHistoryUiState.Success(
                            courseBooks = courseBooks,
                            selectedCourseBook = courseBooks[0],
                            diarySummariesByCourseBook = diarySummariesByCourseBook,
                        )
                    }
                }
                .onFailure { error ->
                    val displayMessage = displayMessageResolver.getDisplayMessage(error)
                    _uiEvent.emit(DiaryHistoryUiEvent.ShowToast(displayMessage))
                    _uiState.update { DiaryHistoryUiState.Error }
                }
        }
    }

    private fun convertToDiarySummariesByCourseBook(
        courseBookDiarySubmissionsList: List<CourseBookDiarySubmissions>,
    ): Map<CourseBook, Map<LocalDate, Selectable<List<DiarySummary>>>> = courseBookDiarySubmissionsList.associate { courseBookDiarySubmissions ->
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

    fun selectCourseBook(coursebookIndex: Int) {
        _uiState.update { state ->
            when (state) {
                is DiaryHistoryUiState.Success -> state.copy(
                    selectedCourseBook = state.courseBooks[coursebookIndex],
                )

                else -> state
            }
        }
    }

    fun toggleDateExpand(date: LocalDate) {
        _uiState.update { state ->
            when (state) {
                is DiaryHistoryUiState.Success -> {
                    val selectedCourseBook = state.selectedCourseBook
                    val currentDiarySummariesByDate = state.diarySummariesByCourseBook[selectedCourseBook]
                        ?: return@update state

                    val updatedDiarySummariesByDate = currentDiarySummariesByDate + (date to currentDiarySummariesByDate[date]!!.toggle())

                    state.copy(
                        diarySummariesByCourseBook = state.diarySummariesByCourseBook + (selectedCourseBook to updatedDiarySummariesByDate),
                    )
                }

                else -> state
            }
        }
    }

    fun openDeleteDiaryDialog(diary: DiarySummary) {
        _uiState.update { state ->
            when (state) {
                is DiaryHistoryUiState.Success -> state.copy(
                    dialogState = DiaryHistoryUiState.DialogState.DeleteDiary(diary),
                )

                else -> state
            }
        }
    }

    fun dismissDialog() {
        _uiState.update { state ->
            when (state) {
                is DiaryHistoryUiState.Success -> state.copy(
                    dialogState = DiaryHistoryUiState.DialogState.None,
                )

                else -> state
            }
        }
    }

    fun confirmDeleteDiary(diary: DiarySummary) {
        viewModelScope.launch {
            diaryRepository.removeDiarySubmission(diary)
                .onSuccess {
                    _uiState.update { state ->
                        when (state) {
                            is DiaryHistoryUiState.Success -> {
                                val updatedDiarySummariesByCourseBook = state.diarySummariesByCourseBook
                                    .mapValues { (_, diarySummariesByDate) ->
                                        diarySummariesByDate
                                            .mapValues { (_, selectableDiaryList) ->
                                                val filteredList = selectableDiaryList.item.filter { it.id != diary.id }
                                                selectableDiaryList.copy(item = filteredList)
                                            }
                                            .filterValues { it.item.isNotEmpty() }
                                    }

                                state.copy(
                                    diarySummariesByCourseBook = updatedDiarySummariesByCourseBook,
                                    dialogState = DiaryHistoryUiState.DialogState.None,
                                )
                            }

                            else -> state
                        }
                    }
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
