package com.wafflestudio.snutt2.views.logged_in.vacancy_noti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepository
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.anySelected
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.lib.toggleWhen
import com.wafflestudio.snutt2.lib.unselectAll
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VacancyViewModelNew @Inject constructor(
    private val vacancyRepository: VacancyRepository,
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
    private val remoteConfig: RemoteConfig,
) : ViewModel() {
    private val _vacancyUiEvent: MutableSharedFlow<VacancyUiEvent> = MutableSharedFlow(replay = 1)
    private val _vacancyUiState = MutableStateFlow<VacancyUiState>(VacancyUiState.Loading)

    val vacancyUiEvent = _vacancyUiEvent.asSharedFlow()
    val vacancyUiState = _vacancyUiState.asStateFlow()

    init {
        if (vacancyRepository.firstVacancyVisit.value) {
            showIntroDialog()
        }

        viewModelScope.launch {
            loadVacancyLectures()
        }
    }

    // NOTE: loadVacancyLectures + refresh 효과 상태 관기
    fun reloadVacancyLectures() {
        viewModelScope.launch {
            _vacancyUiState.update { it.copyWithRefreshing(true) }
            loadVacancyLectures()
            _vacancyUiState.update { it.copyWithRefreshing(false) }
        }
    }

    private suspend fun loadVacancyLectures() {
        vacancyRepository.getVacancyLecturesNew()
            .onSuccess { data ->
                if (data.isEmpty()) {
                    _vacancyUiState.emit(
                        VacancyUiState.Empty(
                            dialogState = VacancyUiState.DialogState.None,
                            isRefreshing = false,
                        ),
                    )
                } else {
                    _vacancyUiState.emit(
                        VacancyUiState.Success(
                            vacancyLecturesWithSelection = data
                                .sortedByDescending { it.wasFull && it.registrationCount < it.quota }
                                .map { it.toDataWithState(false) },
                            dialogState = VacancyUiState.DialogState.None,
                            isEditMode = false,
                            isRefreshing = false,
                            deleteButtonEnabled = false,
                        ),
                    )
                }
            }
            .onFailure { error ->
                _vacancyUiState.update { VacancyUiState.Error }
                handleVacancyError(error)
            }
    }

    fun showIntroDialog() {
        _vacancyUiState.update { state ->
            when (state) {
                is VacancyUiState.Success -> state.copy(dialogState = VacancyUiState.DialogState.Intro)
                is VacancyUiState.Empty -> state.copy(dialogState = VacancyUiState.DialogState.Intro)
                else -> state
            }
        }
    }

    fun showDeleteDialog() {
        _vacancyUiState.update { state ->
            when (state) {
                is VacancyUiState.Success -> state.copy(dialogState = VacancyUiState.DialogState.ConfirmDeleteSelected)
                else -> state
            }
        }
    }

    fun dismissDialog() {
        val wasIntro = when (val state = _vacancyUiState.value) {
            is VacancyUiState.Success -> state.dialogState is VacancyUiState.DialogState.Intro
            is VacancyUiState.Empty -> state.dialogState is VacancyUiState.DialogState.Intro
            else -> false
        }
        _vacancyUiState.update { state ->
            when (state) {
                is VacancyUiState.Success -> state.copy(dialogState = VacancyUiState.DialogState.None)
                is VacancyUiState.Empty -> state.copy(dialogState = VacancyUiState.DialogState.None)
                else -> state
            }
        }
        if (wasIntro) {
            viewModelScope.launch {
                vacancyRepository.setVacancyVisited()
                // TODO: 에러 처리?
            }
        }
    }

    fun toggleEditMode() {
        _vacancyUiState.update { state ->
            when (state) {
                is VacancyUiState.Success -> state.copy(
                    isEditMode = state.isEditMode.not(),
                    vacancyLecturesWithSelection = state.vacancyLecturesWithSelection.unselectAll(),
                )
                else -> state
            }
        }
    }

    fun toggleLectureSelected(lectureId: String) {
        _vacancyUiState.update { state ->
            when (state) {
                is VacancyUiState.Success -> {
                    val newVacancyLecturesState =
                        state.vacancyLecturesWithSelection.toggleWhen { it.id == lectureId }
                    state.copy(
                        vacancyLecturesWithSelection = newVacancyLecturesState,
                        deleteButtonEnabled = newVacancyLecturesState.anySelected(),
                    )
                }
                else -> state
            }
        }
    }

    fun deleteSelectedLectures() {
        val state = _vacancyUiState.value
        if (state !is VacancyUiState.Success) return

        _vacancyUiState.update { s ->
            when (s) {
                is VacancyUiState.Success -> s.copy(dialogState = VacancyUiState.DialogState.None)
                else -> s
            }
        }

        viewModelScope.launch {
            state.vacancyLecturesWithSelection.filter { it.state }.map { it.item.id }.forEach {
                // FIXME: 이렇게 하면 리스트에서 여러 번 실패할 때 handleVacancyError 도 와바바박 되는 거 아닌가?
                vacancyRepository.removeVacancyLectureNew(it)
                    .onFailure { error ->
                        handleVacancyError(error)
                    }
            }
            // FIXME: 전체 리스트에 대해 성공했을 때만 아래 두 동작을 하고 싶은데, List<Result> 에 대한 유틸이 있으면 좋겠다.
            toggleEditMode()
            loadVacancyLectures()
        }
    }

    fun openSugangSnu() {
        viewModelScope.launch {
            remoteConfig.sugangSNUUrl.firstOrNull()?.let { url ->
                _vacancyUiEvent.emit(VacancyUiEvent.OpenWebPage(url))
            }
            // FIXME: sugangSNUUrl 를 불러오지 못했을 때 유저에게 피드백이 필요할까? (에러 토스트라던지)
        }
    }

    private suspend fun handleVacancyError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _vacancyUiEvent.emit(VacancyUiEvent.ShowToast(displayMessage))
                userRepository.postForceLogout()
                _vacancyUiEvent.emit(VacancyUiEvent.LoggedOut)
            }

            else -> {
                _vacancyUiEvent.emit(VacancyUiEvent.ShowToast(displayMessage))
            }
        }
    }
}

sealed interface VacancyUiState {
    sealed interface DialogState {
        data object None : DialogState
        data object Intro : DialogState
        data object ConfirmDeleteSelected : DialogState
    }

    data class Success(
        val vacancyLecturesWithSelection: List<Selectable<SearchedLecture>>,
        val dialogState: DialogState,
        val isEditMode: Boolean,
        val isRefreshing: Boolean,
        val deleteButtonEnabled: Boolean,
    ) : VacancyUiState

    data object Error : VacancyUiState
    data object Loading : VacancyUiState
    data class Empty(
        val dialogState: DialogState,
        val isRefreshing: Boolean,
    ) : VacancyUiState
}

sealed interface VacancyUiEvent {
    data class ShowToast(val message: String) : VacancyUiEvent
    data object LoggedOut : VacancyUiEvent
    data class OpenWebPage(val url: String) : VacancyUiEvent
}

// FIXME: 그냥 그때그때 분기하기 vs 이렇게 유틸 만들기 뭐가 나을까?
private fun VacancyUiState.copyWithRefreshing(isRefreshing: Boolean): VacancyUiState = when (this) {
    is VacancyUiState.Success -> copy(isRefreshing = isRefreshing)
    is VacancyUiState.Empty -> copy(isRefreshing = isRefreshing)
    is VacancyUiState.Error -> this
    is VacancyUiState.Loading -> this
}
