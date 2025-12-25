package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_write

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.lecture_diary.DiaryRepository
import com.wafflestudio.snutt2.data.user.UserRepository
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

    private var nextLectureId: String? = null
    private var nextLectureTitle: String? = null

    init {
        viewModelScope.launch {
            val lectureId = savedStateHandle.get<String>("lectureId")
            val courseTitle = savedStateHandle.get<String>("courseTitle")

            // GET /v1/diary/dailyClassTypes 호출
            diaryRepository.getDailyClassTypes()
                .onSuccess { dailyClassTypes ->
                    _uiState.value = DiaryWriteUiState.Write(
                        lectureName = courseTitle ?: "",
                        dailyClassTypes = dailyClassTypes.map { Selectable(it, false) },
                        activitySelectingState = ActivitySelectionState.InitialSelecting,
                        questions = emptyList(),
                    )
                }
                .onFailure { error ->
                    handleDiaryWriteError(error)
                    _uiState.value = DiaryWriteUiState.Error
                }
        }
    }

    private val _uiEvent: MutableSharedFlow<DiaryWriteUiEvent> =
        MutableSharedFlow(1)
    val uiEvent: SharedFlow<DiaryWriteUiEvent> =
        _uiEvent.asSharedFlow()

    fun toggleActivitySelection(index: Int) {
        // 로컬 변수 둬도 뭐 큰 문제 없겠지?
        val state = _uiState.value
        if (state !is DiaryWriteUiState.Write) return

        _uiState.value = state.copyWith(
            dailyClassTypes = state.dailyClassTypes.toggleIndex(index),
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
            (_uiState.value as? DiaryWriteUiState.Write)?.copyWith(activitySelectingState = newState)
                ?: return
    }

    fun toggleAnswer(questionIndex: Int, answerIndex: Int) {
        val state = _uiState.value
        if (state !is DiaryWriteUiState.Write) return

        _uiState.value = state.copyWith(
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
            val state = _uiState.value
            if (state !is DiaryWriteUiState.Write) return@launch

            val lectureId = savedStateHandle.get<String>("lectureId") ?: return@launch

            // POST /v1/diary도 name을 사용
            val selectedDailyClassTypeNames = state.dailyClassTypes
                .filter { it.isSelected() }
                .map { it.item.name }

            val questionAnswers = state.questions.mapIndexed { questionIndex, question ->
                val answerIndex = question.selectableAnswers.indexOfFirst { it.isSelected() }
                com.wafflestudio.snutt2.domainmodel.diary.DiaryAnsweredQuestion(
                    questionId = question.id,
                    answerIndex = answerIndex,
                )
            }

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
                    _uiState.value = DiaryWriteUiState.Complete(nextAction)
                }
                .onFailure { error ->
                    handleDiaryWriteError(error)
                }
        }
    }

    fun completeActivitySelection() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state !is DiaryWriteUiState.Write) return@launch

            val lectureId = savedStateHandle.get<String>("lectureId") ?: return@launch
            // POST /v1/diary/questionnaire는 name을 사용
            val selectedDailyClassTypeNames = state.dailyClassTypes
                .filter { it.isSelected() }
                .map { it.item.name }

            // InitialSelecting과 ReSelecting 모두 동일한 처리
            if (state.activitySelectingState.isSelecting()) {
                diaryRepository.getQuestionnaire(
                    lectureId = lectureId,
                    dailyClassTypes = selectedDailyClassTypeNames,
                )
                    .onSuccess { questionnaireData ->
                        nextLectureId = questionnaireData.nextLectureId
                        nextLectureTitle = questionnaireData.nextLectureTitle
                        _uiState.value = state.copyWith(
                            activitySelectingState = ActivitySelectionState.Complete,
                            questions = questionnaireData.questions,
                        )
                    }
                    .onFailure { error ->
                        handleDiaryWriteError(error)
                    }
            }
        }
    }

    fun writeNextDiary() {
        viewModelScope.launch {
            val lectureId = nextLectureId ?: return@launch
            val courseTitle = nextLectureTitle ?: return@launch

            // savedStateHandle 업데이트
            savedStateHandle["lectureId"] = lectureId
            savedStateHandle["courseTitle"] = courseTitle

            // 다음 강의를 위한 정보 초기화
            nextLectureId = null
            nextLectureTitle = null

            // GET /v1/diary/dailyClassTypes 호출
            diaryRepository.getDailyClassTypes()
                .onSuccess { dailyClassTypes ->
                    _uiState.value = DiaryWriteUiState.Write(
                        lectureName = courseTitle,
                        dailyClassTypes = dailyClassTypes.map { Selectable(it, false) },
                        activitySelectingState = ActivitySelectionState.InitialSelecting,
                        questions = emptyList(),
                    )
                }
                .onFailure { error ->
                    handleDiaryWriteError(error)
                    _uiState.value = DiaryWriteUiState.Error
                }
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

    // TODO: 이름 컨벤션 논의
    data object ForceLogout : DiaryWriteUiEvent
    data object Return : DiaryWriteUiEvent
}
