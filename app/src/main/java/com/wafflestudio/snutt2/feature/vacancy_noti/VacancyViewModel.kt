package com.wafflestudio.snutt2.feature.vacancy_noti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepository
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.anySelected
import com.wafflestudio.snutt2.domain.AuthError
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.lib.toggleWhen
import com.wafflestudio.snutt2.lib.unselectAll
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VacancyViewModel @Inject constructor(
    private val vacancyRepository: VacancyRepository,
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
    private val remoteConfig: RemoteConfig,
) : ViewModel() {
    private val _vacancyUiEvent: MutableSharedFlow<VacancyUiEvent> = MutableSharedFlow(replay = 1)
    private val _vacancyUiState = MutableStateFlow(VacancyUiState())

    val vacancyUiEvent = _vacancyUiEvent.asSharedFlow()
    val vacancyUiState = _vacancyUiState.asStateFlow()

    init {
        if (vacancyRepository.firstVacancyVisit.value) {
            showIntroDialog()
            viewModelScope.launch { vacancyRepository.setVacancyVisited() }
        }

        // B-1 + A: 초기 fetch 후 vacancyLectures 구독
        viewModelScope.launch {
            flow {
                when (val result = vacancyRepository.fetchVacancyLectures()) {
                    is Result.Success -> emitAll(vacancyRepository.vacancyLectures)
                    is Result.Fail -> {
                        _vacancyUiState.update { it.copy(contentState = VacancyUiState.ContentState.Error) }
                        handleVacancyError(result.error)
                    }
                }
            }.collect { lectures ->
                _vacancyUiState.update {
                    it.copy(
                        contentState = if (lectures.isEmpty()) {
                            VacancyUiState.ContentState.Empty
                        } else {
                            VacancyUiState.ContentState.Loaded(
                                vacancyLecturesWithSelection = lectures
                                    .sortedByDescending { l -> l.wasFull && l.registrationCount < l.quota }
                                    .map { l -> l.toDataWithState(false) },
                            )
                        },
                    )
                }
            }
        }
    }

    fun reloadVacancyLectures() {
        viewModelScope.launch {
            _vacancyUiState.update { it.copy(isRefreshing = true) }
            vacancyRepository.fetchVacancyLectures()
                .onFailure { error ->
                    _vacancyUiState.update { it.copy(contentState = VacancyUiState.ContentState.Error) }
                    handleVacancyError(error)
                }
            _vacancyUiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun showIntroDialog() {
        _vacancyUiState.update { it.copy(dialogState = VacancyUiState.DialogState.Intro) }
    }

    fun showDeleteDialog() {
        _vacancyUiState.update { it.copy(dialogState = VacancyUiState.DialogState.ConfirmDeleteSelected) }
    }

    fun dismissDialog() {
        _vacancyUiState.update { it.copy(dialogState = VacancyUiState.DialogState.None) }
    }

    fun toggleEditMode() {
        _vacancyUiState.update { state ->
            val content = state.contentState
            if (content is VacancyUiState.ContentState.Loaded) {
                state.copy(
                    isEditMode = !state.isEditMode,
                    contentState = content.copy(
                        vacancyLecturesWithSelection = content.vacancyLecturesWithSelection.unselectAll(),
                        deleteButtonEnabled = false,
                    ),
                )
            } else {
                state
            }
        }
    }

    fun toggleLectureSelected(lectureId: String) {
        _vacancyUiState.update { state ->
            val content = state.contentState
            if (content is VacancyUiState.ContentState.Loaded) {
                val newList = content.vacancyLecturesWithSelection.toggleWhen { it.id == lectureId }
                state.copy(
                    contentState = content.copy(
                        vacancyLecturesWithSelection = newList,
                        deleteButtonEnabled = newList.anySelected(),
                    ),
                )
            } else {
                state
            }
        }
    }

    fun deleteSelectedLectures() {
        val content = _vacancyUiState.value.contentState
        if (content !is VacancyUiState.ContentState.Loaded) return

        _vacancyUiState.update { it.copy(dialogState = VacancyUiState.DialogState.None) }

        viewModelScope.launch {
            content.vacancyLecturesWithSelection.filter { it.state }.map { it.item }.forEach {
                vacancyRepository.removeVacancyLecture(it)
                    .onFailure { error -> handleVacancyError(error) }
            }
        }
    }

    fun openSugangSnu() {
        viewModelScope.launch {
            remoteConfig.sugangSNUUrl.firstOrNull()?.let { url ->
                _vacancyUiEvent.emit(VacancyUiEvent.OpenWebPage(url))
            }
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

data class VacancyUiState(
    val contentState: ContentState = ContentState.Loading,
    val dialogState: DialogState = DialogState.None,
    val isEditMode: Boolean = false,
    val isRefreshing: Boolean = false,
) {
    sealed interface ContentState {
        data object Loading : ContentState
        data object Error : ContentState
        data object Empty : ContentState
        data class Loaded(
            val vacancyLecturesWithSelection: List<Selectable<SearchedLecture>>,
            val deleteButtonEnabled: Boolean = false,
        ) : ContentState
    }

    sealed interface DialogState {
        data object None : DialogState
        data object Intro : DialogState
        data object ConfirmDeleteSelected : DialogState
    }
}

sealed interface VacancyUiEvent {
    data class ShowToast(val message: String) : VacancyUiEvent
    data object LoggedOut : VacancyUiEvent
    data class OpenWebPage(val url: String) : VacancyUiEvent
}

