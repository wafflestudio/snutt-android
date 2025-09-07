package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_write

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.lecture_diary.DiaryRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.toggleIndex
import com.wafflestudio.snutt2.lib.unselectExcept
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<DiaryWriteUiState>(
            DiaryWriteUiState.Loading,
        )
    val uiState: StateFlow<DiaryWriteUiState> =
        _uiState.asStateFlow()

    init {
        // TODO: 구현
//        val lectureId = savedStateHandle.get<String>("lectureId")
        _uiState.value = DiaryMockData.initialWriteUiState
    }

    private val _uiEvent: MutableSharedFlow<DiaryWriteUiEvent> =
        MutableSharedFlow(1)
    val uiEvent: SharedFlow<DiaryWriteUiEvent> =
        _uiEvent.asSharedFlow()

    fun toggleActivitySelection(index: Int) {
        // 로컬 변수 둬도 뭐 큰 문제 없겠지?
        val state = _uiState.value
        if (state !is DiaryWriteUiState.Write) return

        _uiState.value = state.copy(
            activities = state.activities.toggleIndex(index),
            activitySelectingState = when (state.activitySelectingState) {
                ActivitySelectionState.InitialSelecting -> ActivitySelectionState.InitialSelecting
                ActivitySelectionState.Complete -> ActivitySelectionState.ReSelecting
                ActivitySelectionState.ReSelecting -> ActivitySelectionState.ReSelecting
            },
        )
    }

    fun setSelectingActivitiesState(newState: ActivitySelectionState) {
        // 이거 안 해줬을 때 recompose skip 되는지 확인하기
        // @Stable 같은 거 해줘야 할지도...
        if ((_uiState.value as? DiaryWriteUiState.Write)?.activitySelectingState == newState) {
            return
        }

        // 로컬 변수 안 두면 이렇게 해야 하는데...
        _uiState.value =
            (_uiState.value as? DiaryWriteUiState.Write)?.copy(activitySelectingState = newState)
                ?: return
    }

    fun toggleAnswer(questionIndex: Int, answerIndex: Int) {
        val state = _uiState.value
        if (state !is DiaryWriteUiState.Write) return

        _uiState.value = state.copy(
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
    }

    // FIXME: comment 한 글자 한 글자 바뀌는 것도 전부 상태 hoist 하기 vs 로컬 상태로 뒀다가 한번에 제출하기
    // 후자는 일관성이 깨지는 느낌이 있는데...
    fun saveDiaryWrite(comment: String) {
        viewModelScope.launch {
            // TODO: 구현
//            diaryRepository.saveDiaryWrite(diaryWriteData)
//                // TODO: 등록완료 화면으로 상태전환하기
//                .onFailure {
//                    _diaryWriteInit.emit(DiaryWriteUiState.Error)
//                }
            _uiState.value = DiaryWriteUiState.Complete(DiaryNextAction.WriteReview)
        }
    }

    fun writeNextDiary() {
        // TODO: 구현
        _uiState.value = DiaryMockData.initialWriteUiState
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

    // TODO: 이름 컨벤션 논의
    data object ForceLogout : DiaryWriteUiEvent
}
