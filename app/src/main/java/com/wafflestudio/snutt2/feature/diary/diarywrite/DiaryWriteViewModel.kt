package com.wafflestudio.snutt2.feature.diary.diarywrite

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.lecturediary.DiaryRepository
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.AuthError
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.domain.model.diary.DiaryAnsweredQuestion
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.isSelected
import com.wafflestudio.snutt2.lib.toggleIndex
import com.wafflestudio.snutt2.lib.unselectExcept
import com.wafflestudio.snutt2.logging.AnalyticsEvent
import com.wafflestudio.snutt2.logging.AnalyticsLogger
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.DiaryAfterSubmitParameter
import com.wafflestudio.snutt2.logging.DiarySubmittedParameter
import com.wafflestudio.snutt2.logging.DiaryWriteExitParameter
import com.wafflestudio.snutt2.logging.DiaryWriteParameter
import com.wafflestudio.snutt2.logging.DiaryWriteReferrer
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
import kotlin.time.TimeSource

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
    private val displayMessageResolver: DisplayMessageResolver,
    private val analyticsLogger: AnalyticsLogger,
    timeSource: TimeSource,
) : ViewModel() {
    private val lectureId: String
        get() = savedStateHandle.get<String>("lectureId") ?: ""
    private val courseTitle: String
        get() = savedStateHandle.get<String>("courseTitle") ?: ""
    private val referrer: DiaryWriteReferrer = DiaryWriteReferrer.decode(savedStateHandle.get<String>("referrer"))

    val analyticsScreen = AnalyticsScreen.DiaryWrite(DiaryWriteParameter(referrer))

    private val enteredAt = timeSource.markNow()
    private var isExitLogged = false

    private val _uiState =
        MutableStateFlow<DiaryWriteUiState>(DiaryWriteUiState.Loading)
    val uiState: StateFlow<DiaryWriteUiState> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<DiaryWriteUiEvent> = MutableSharedFlow(1)
    val uiEvent: SharedFlow<DiaryWriteUiEvent> = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            diaryRepository.getDailyClassTypes()
                .onSuccess { dailyClassTypes ->
                    _uiState.update {
                        DiaryWriteUiState.Write(
                            lectureName = courseTitle,
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

    fun onCommentTextChange(value: String) {
        _uiState.update { state ->
            when (state) {
                is DiaryWriteUiState.Write -> state.copy(commentText = value)
                else -> state
            }
        }
    }

    fun saveDiaryWrite() {
        val state = _uiState.value as? DiaryWriteUiState.Write ?: return
        val selectedDailyClassTypes = state.dailyClassTypes
            .filter { it.isSelected() }
            .map { it.item }
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
                dailyClassTypes = selectedDailyClassTypes,
                questionAnswers = questionAnswers,
                comment = state.commentText,
            )
                .onSuccess {
                    analyticsLogger.logEvent(
                        AnalyticsEvent.DiarySubmitted(
                            DiarySubmittedParameter(
                                activityTypeIds = selectedDailyClassTypes.map { it.id },
                                hasComment = state.commentText.isNotBlank(),
                                elapsedSec = elapsedSec(),
                                referrer = referrer,
                            ),
                        ),
                    )
                    val nextLecture = state.nextLecture
                    val nextAction = if (nextLecture != null) {
                        DiaryNextAction.WriteNext(nextLecture.lectureId, nextLecture.courseTitle)
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

    /**
     * 작성 화면을 닫는다. 제출 전(Write 상태)에 닫는 경우에만 이탈 로그를 한 번 남긴다.
     */
    fun onExitWithoutSubmit() {
        val state = _uiState.value
        if (state is DiaryWriteUiState.Write && !isExitLogged) {
            isExitLogged = true
            val step = if (state.activitySelectingState == ActivitySelectionState.InitialSelecting) {
                DiaryWriteExitParameter.Step.ACTIVITY
            } else {
                DiaryWriteExitParameter.Step.QUESTIONS
            }
            analyticsLogger.logEvent(
                AnalyticsEvent.DiaryWriteExit(
                    DiaryWriteExitParameter(
                        step = step,
                        elapsedSec = elapsedSec(),
                        referrer = referrer,
                    ),
                ),
            )
        }
        viewModelScope.launch { _uiEvent.emit(DiaryWriteUiEvent.Return) }
    }

    fun completeActivitySelection() {
        val state = _uiState.value as? DiaryWriteUiState.Write ?: return
        if (!state.activitySelectingState.isSelecting()) return
        analyticsLogger.logEvent(AnalyticsEvent.DiaryFirstSectionDone)
        val selectedDailyClassTypes = state.dailyClassTypes
            .filter { it.isSelected() }
            .map { it.item }
        viewModelScope.launch {
            diaryRepository.getQuestionnaire(
                lectureId = lectureId,
                dailyClassTypes = selectedDailyClassTypes,
            )
                .onSuccess { questionnaireData ->
                    _uiState.update { current ->
                        when (current) {
                            is DiaryWriteUiState.Write -> current.copy(
                                activitySelectingState = ActivitySelectionState.Complete,
                                questions = questionnaireData.questions,
                                nextLecture = questionnaireData.nextLectureId?.let { id ->
                                    NextLecture(id, questionnaireData.nextLectureTitle ?: "")
                                },
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

    fun writeNextDiary() {
        val state = _uiState.value as? DiaryWriteUiState.Complete ?: return
        val writeNext = state.nextAction as? DiaryNextAction.WriteNext ?: return
        analyticsLogger.logEvent(AnalyticsEvent.DiaryAfterSubmit(DiaryAfterSubmitParameter(DiaryAfterSubmitParameter.Action.NEXT)))
        viewModelScope.launch {
            _uiEvent.emit(
                DiaryWriteUiEvent.NextDiary(
                    writeNext.lectureId,
                    writeNext.courseTitle,
                ),
            )
        }
    }

    fun goReview() {
        analyticsLogger.logEvent(AnalyticsEvent.DiaryAfterSubmit(DiaryAfterSubmitParameter(DiaryAfterSubmitParameter.Action.REVIEW)))
        viewModelScope.launch { _uiEvent.emit(DiaryWriteUiEvent.NavigateReview) }
    }

    fun goHome() {
        analyticsLogger.logEvent(AnalyticsEvent.DiaryAfterSubmit(DiaryAfterSubmitParameter(DiaryAfterSubmitParameter.Action.HOME)))
        viewModelScope.launch { _uiEvent.emit(DiaryWriteUiEvent.NavigateHome) }
    }

    private fun elapsedSec(): Long = enteredAt.elapsedNow().inWholeSeconds

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
    data class ShowToast(val message: String) : DiaryWriteUiEvent

    data class NextDiary(
        val lectureId: String,
        val courseTitle: String,
    ) : DiaryWriteUiEvent

    data object NavigateReview : DiaryWriteUiEvent
    data object NavigateHome : DiaryWriteUiEvent
    data object ForceLogout : DiaryWriteUiEvent
    data object Return : DiaryWriteUiEvent
}
