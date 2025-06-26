package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.lecture_diary.DiaryRepository
import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _diaryWriteInit = MutableStateFlow<DiaryWriteUiState>(DiaryWriteUiState.Loading)
    val diaryWriteInit = _diaryWriteInit.asStateFlow()

    private val _diaryWriteUiEvent: MutableSharedFlow<DiaryWriteUiEvent> = MutableSharedFlow(1)
    val diaryWriteUiEvent: SharedFlow<DiaryWriteUiEvent> = _diaryWriteUiEvent

    init {
        viewModelScope.launch {
            _diaryWriteInit.value = DiaryWriteUiState.Loading
            diaryRepository.getDiaryWriteInit()
                .onSuccess { data ->
                    _diaryWriteInit.emit(DiaryWriteUiState.Success(data))
                }.onFailure { error ->
                    _diaryWriteInit.emit(DiaryWriteUiState.Error)
                    handleDiaryWriteError(error)
                }
        }
    }

    fun saveDiaryWrite(diaryWriteData: DiaryWrite) {
        viewModelScope.launch {
            diaryRepository.saveDiaryWrite(diaryWriteData)
                .onSuccess {
                    _diaryWriteUiEvent.emit(DiaryWriteUiEvent.NavigateToWriteMore)
                }
                .onFailure {
                    _diaryWriteInit.emit(DiaryWriteUiState.Error)
                }
        }
    }

    private suspend fun handleDiaryWriteError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _diaryWriteUiEvent.emit(DiaryWriteUiEvent.ShowToast(displayMessage))
                diaryRepository.clearToken()
                _diaryWriteUiEvent.emit(DiaryWriteUiEvent.NavigateToOnboard)
            }
            else -> {
                _diaryWriteUiEvent.emit(DiaryWriteUiEvent.ShowToast(displayMessage))
            }
        }
    }
}

sealed interface DiaryWriteUiEvent {
    data class ShowToast(val message: String) : DiaryWriteUiEvent
    data object NavigateToWriteMore : DiaryWriteUiEvent
    data object NavigateToOnboard : DiaryWriteUiEvent
}
