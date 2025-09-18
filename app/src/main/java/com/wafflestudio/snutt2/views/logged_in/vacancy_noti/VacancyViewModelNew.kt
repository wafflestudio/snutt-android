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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VacancyViewModelNew @Inject constructor(
    private val vacancyRepository: VacancyRepository,
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
    private val remoteConfig: RemoteConfig,
) : ViewModel() {
    private val _vacancyUiState = MutableStateFlow(VacancyUiStateTypes.Loading)
    private val _isEditMode = MutableStateFlow(false)
    private val _vacancyLecturesWithSelection =
        MutableStateFlow<List<Selectable<SearchedLecture>>>(listOf())
    private val _isRefreshing = MutableStateFlow(false)
    private val _firstVacancyVisit = vacancyRepository.firstVacancyVisit
    private val _showIntroDialog = MutableStateFlow(false)

    private val _vacancyUiEvent: MutableSharedFlow<VacancyUiEvent> = MutableSharedFlow(replay = 1)
    val vacancyUiEvent = _vacancyUiEvent.asSharedFlow()
    val vacancyUiState: StateFlow<VacancyUiState> = combine(
        _vacancyUiState,
        _isEditMode,
        _vacancyLecturesWithSelection,
        _isRefreshing,
        _firstVacancyVisit,
        _showIntroDialog,
    ) { vacancyUiState, isEditMode, vacancyLecturesWithSelection, isRefreshing, firstVacancyVisit, showIntroDialog ->
        val showIntroDialogCombine = when {
            firstVacancyVisit -> true
            showIntroDialog -> true
            else -> false
        }
        when (vacancyUiState) {
            VacancyUiStateTypes.Loading -> VacancyUiState.Loading
            VacancyUiStateTypes.Error -> VacancyUiState.Error
            VacancyUiStateTypes.Empty -> VacancyUiState.Empty(
                showIntroDialog = showIntroDialogCombine,
                isRefreshing = isRefreshing,
            )

            VacancyUiStateTypes.Success -> VacancyUiState.Success(
                vacancyLectures = vacancyLecturesWithSelection,
                showIntroDialog = showIntroDialogCombine,
                isEditMode = isEditMode,
                isRefreshing = isRefreshing,
                deleteButtonEnabled = isEditMode && vacancyLecturesWithSelection.anySelected(),
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        VacancyUiState.Loading,
    )

    init {
        loadVacancyLectures()
    }

    fun fetchVacancyLectures() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            loadVacancyLectures()
            _isRefreshing.emit(false)
        }
    }

    private fun loadVacancyLectures() {
        viewModelScope.launch {
            vacancyRepository.getVacancyLecturesNew()
                .onSuccess { data ->
                    if (data.isEmpty()) {
                        _vacancyUiState.emit(VacancyUiStateTypes.Empty)
                    } else {
                        _vacancyUiState.emit(VacancyUiStateTypes.Success)
                    }
                    _vacancyLecturesWithSelection.emit(
                        data
                            .sortedByDescending { it.wasFull && it.registrationCount < it.quota }
                            .map { it.toDataWithState(true) },
                    )
                }
                .onFailure { error ->
                    _vacancyUiState.emit(VacancyUiStateTypes.Error)
                    handleVacancyError(error)
                }
        }
    }

    fun showIntroDialog() {
        viewModelScope.launch {
            _showIntroDialog.emit(true)
        }
    }

    fun hideIntroDialog() {
        viewModelScope.launch {
            setVacancyVisited()
            _showIntroDialog.emit(false)
        }
    }

    private fun setVacancyVisited() {
        viewModelScope.launch {
            if (_firstVacancyVisit.value) {
                vacancyRepository.setVacancyVisited()
            }
        }
    }

    fun toggleEditMode() {
        viewModelScope.launch {
            _isEditMode.emit(_isEditMode.value.not())
            _vacancyLecturesWithSelection.value = _vacancyLecturesWithSelection.value.unselectAll()
        }
    }

    fun toggleLectureSelected(lectureId: String) {
        _vacancyLecturesWithSelection.value = _vacancyLecturesWithSelection.value.toggleWhen {
            it.id === lectureId
        }
    }

    fun deleteSelectedLectures() {
        viewModelScope.launch {
            toggleEditMode()
            _vacancyLecturesWithSelection.value.filter { it.state }.map { it.item.id }
                .forEach { lectureId ->
                    vacancyRepository.removeVacancyLectureNew(lectureId)
                        .onFailure { error ->
                            handleVacancyError(error)
                        }
                }
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

private enum class VacancyUiStateTypes {
    Loading, Error, Empty, Success
}

sealed interface VacancyUiState {
    data class Success(
        val vacancyLectures: List<Selectable<SearchedLecture>>,
        val showIntroDialog: Boolean,
        val isEditMode: Boolean,
        val isRefreshing: Boolean,
        val deleteButtonEnabled: Boolean,
    ) : VacancyUiState

    data object Error : VacancyUiState
    data object Loading : VacancyUiState
    data class Empty(
        val showIntroDialog: Boolean,
        val isRefreshing: Boolean,
    ) : VacancyUiState
}

sealed interface VacancyUiEvent {
    data class ShowToast(val message: String) : VacancyUiEvent
    data object LoggedOut : VacancyUiEvent
    data class OpenWebPage(val url: String) : VacancyUiEvent
}

// combine은 기본적으로 5개까지만 지원한다.
fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R,
): Flow<R> = combine(
    combine(flow1, flow2, flow3, ::Triple),
    combine(flow4, flow5, flow6, ::Triple),
) { triple1, triple2 ->
    transform(
        triple1.first, triple1.second, triple1.third,
        triple2.first, triple2.second, triple2.third,
    )
}
