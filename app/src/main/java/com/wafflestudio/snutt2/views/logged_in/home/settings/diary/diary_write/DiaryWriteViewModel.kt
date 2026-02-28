package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_write

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.lecture_diary.DiaryRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.diary.DiaryAnsweredQuestion
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.isSelected
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import com.wafflestudio.snutt2.lib.toggleIndex
import com.wafflestudio.snutt2.lib.unselectExcept
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {
    private val lectureId: String
        get() = savedStateHandle.get<String>("lectureId") ?: ""
    private val courseTitle: String
        get() = savedStateHandle.get<String>("courseTitle") ?: ""

    private val _uiState =
        MutableStateFlow<DiaryWriteUiState>(DiaryWriteUiState.Loading)
    val uiState: StateFlow<DiaryWriteUiState> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<DiaryWriteUiEvent> = MutableSharedFlow(1)
    val uiEvent: SharedFlow<DiaryWriteUiEvent> = _uiEvent.asSharedFlow()

    // FIXME: 얘의 존재에 대해서는 나중에 다시 고민하기
    private var nextLectureId: String? = null
    private var nextLectureTitle: String? = null

    init {
        viewModelScope.launch {
            diaryRepository.getDailyClassTypes()
                .onSuccess { dailyClassTypes ->
                    _uiState.update {
                        DiaryWriteUiState.Write(
                            lectureName = courseTitle ?: "",
                            dailyClassTypes = dailyClassTypes.map { Selectable(it, false) },
                            activitySelectingState = ActivitySelectionState.InitialSelecting,
                            questions = emptyList(),
                        )
                    }
                }
                .onFailure { error ->
                    handleDiaryWriteError(error)
                    _uiState.update { DiaryWriteUiState.Error }
                }
        }
    }

    fun toggleActivitySelection(index: Int) {
        _uiState.update { state ->
            when (state) {
                is DiaryWriteUiState.Write -> state.copy(
                    dailyClassTypes = state.dailyClassTypes.toggleIndex(index),
                    activitySelectingState = when (state.activitySelectingState) {
                        ActivitySelectionState.InitialSelecting -> ActivitySelectionState.InitialSelecting
                        ActivitySelectionState.Complete -> ActivitySelectionState.ReSelecting
                        ActivitySelectionState.ReSelecting -> ActivitySelectionState.ReSelecting
                    },
                )

                else -> state
            }
        }
    }

    fun setSelectingActivitiesState(newState: ActivitySelectionState) {
        _uiState.update { state ->
            when (state) {
                is DiaryWriteUiState.Write -> {
                    state.copy(activitySelectingState = newState)
                }

                else -> state
            }
        }
    }

    fun toggleAnswer(questionIndex: Int, answerIndex: Int) {
        _uiState.update { state ->
            when (state) {
                is DiaryWriteUiState.Write -> state.copy(
                    questions = state.questions.mapIndexed { index, question ->
                        if (index == questionIndex) {
                            question.copy(
                                selectableAnswers = question.selectableAnswers
                                    .toggleIndex(answerIndex)
                                    .unselectExcept(answerIndex),
                            )
                        } else {
                            question
                        }
                    },
                )

                else -> state
            }
        }
    }

    fun saveDiaryWrite(comment: String) {
        when (val state = _uiState.value) {
            !is DiaryWriteUiState.Write -> {
                return
            }

            is DiaryWriteUiState.Write -> {
                val selectedDailyClassTypeNames = state.dailyClassTypes
                    .filter { it.isSelected() }
                    .map { it.item.name }

                val questionAnswers = state.questions.map { question ->
                    val answerIndex = question.selectableAnswers.indexOfFirst { it.isSelected() }
                    DiaryAnsweredQuestion(
                        questionId = question.id,
                        answerIndex = answerIndex,
                    )
                }

                viewModelScope.launch {
                    diaryRepository.submitDiary(
                        lectureId = lectureId,
                        dailyClassTypes = selectedDailyClassTypeNames,
                        questionAnswers = questionAnswers,
                        comment = comment,
                    )
                        .onSuccess {
                            val nextAction = if (nextLectureId != null) {
                                DiaryNextAction.WriteNext
                            } else {
                                DiaryNextAction.WriteReview
                            }
                            _uiState.update { DiaryWriteUiState.Complete(nextAction) }
                        }
                        .onFailure { error ->
                            handleDiaryWriteError(error)
                        }
                }
            }
        }
    }

    fun completeActivitySelection() {
        when (val state = _uiState.value) {
            !is DiaryWriteUiState.Write -> {
                return
            }

            is DiaryWriteUiState.Write -> {
                val selectedDailyClassTypeNames = state.dailyClassTypes
                    .filter { it.isSelected() }
                    .map { it.item.name }

                // FIXME: 이런 Early return 이, 나중에 읽었을 때 도움이 될까?
                if (state.activitySelectingState.isSelecting().not()) {
                    return
                }

                viewModelScope.launch {
                    diaryRepository.getQuestionnaire(
                        lectureId = lectureId,
                        dailyClassTypes = selectedDailyClassTypeNames,
                    )
                        .onSuccess { questionnaireData ->
                            nextLectureId = questionnaireData.nextLectureId
                            nextLectureTitle = questionnaireData.nextLectureTitle

                            _uiState.update { current ->
                                when (current) {
                                    is DiaryWriteUiState.Write -> current.copy(
                                        activitySelectingState = ActivitySelectionState.Complete,
                                        questions = questionnaireData.questions,
                                    )

                                    else -> current
                                }
                            }
                        }
                        .onFailure { error ->
                            handleDiaryWriteError(error)
                        }
                }
            }
        }
    }

    // FIXME: 이름 이거 맞아?
    fun writeNextDiary() {
        viewModelScope.launch {
            _uiEvent.emit(
                DiaryWriteUiEvent.NextDiary(
                    nextLectureId ?: return@launch,
                    nextLectureTitle ?: return@launch,
                ),
            )
        }
    }

    private suspend fun handleDiaryWriteError(error: DomainError) {
        val displayMessage =
            displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _uiEvent.emit(
                    DiaryWriteUiEvent.ShowToast(
                        displayMessage,
                    ),
                )
                userRepository.performLogout()
                _uiEvent.emit(DiaryWriteUiEvent.ForceLogout)
            }

            else -> {
                _uiEvent.emit(
                    DiaryWriteUiEvent.ShowToast(
                        displayMessage,
                    ),
                )
            }
        }
    }
}

sealed interface DiaryWriteUiEvent {
    data class ShowToast(val message: String) :
        DiaryWriteUiEvent

    data class NextDiary(
        val lectureId: String,
        val courseTitle: String,
    ) : DiaryWriteUiEvent

    data object ForceLogout : DiaryWriteUiEvent
    data object Return : DiaryWriteUiEvent
}
