package com.wafflestudio.snutt2.views.logged_in.vacancy_noti

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepository
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
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
    private val _vacancyLectures = MutableStateFlow<List<SearchedLecture>>(listOf())
    private val _isRefreshing = MutableStateFlow(false)
    private val _firstVacancyVisit = vacancyRepository.firstVacancyVisit
    private val _showIntroDialog = MutableStateFlow(false)
    private val _selectedLectures = mutableStateListOf<String>()
    private val _selectedLecturesFlow = snapshotFlow { _selectedLectures.toList() }

    private val _vacancyUiEvent: MutableSharedFlow<VacancyUiEvent> = MutableSharedFlow(replay = 1)
    val vacancyUiEvent = _vacancyUiEvent.asSharedFlow()

    val sugangSNUUrl = remoteConfig.sugangSNUUrl
    val vacancyUiState: StateFlow<VacancyUiState> = combine(
        _vacancyUiState,
        _isEditMode,
        _vacancyLectures,
        _isRefreshing,
        _firstVacancyVisit,
        _showIntroDialog,
        _selectedLecturesFlow,
    ) { vacancyUiState, isEditMode, vacancyLectures, isRefreshing, firstVacancyVisit, showIntroDialog, selectedLectures ->
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
                vacancyLectures = vacancyLectures,
                showIntroDialog = showIntroDialogCombine,
                isEditMode = isEditMode,
                isRefreshing = isRefreshing,
                selectedLectures = selectedLectures,
                deleteEnabled = isEditMode && selectedLectures.isNotEmpty(),
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
                    _vacancyLectures.emit(
                        data.sortedByDescending { it.wasFull && it.registrationCount < it.quota },
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
            _selectedLectures.clear()
        }
    }

    fun toggleLectureSelected(lectureId: String) {
        if (!_selectedLectures.contains(lectureId)) {
            _selectedLectures.add(lectureId)
        } else {
            _selectedLectures.remove(lectureId)
        }
    }

    fun deleteSelectedLectures() {
        viewModelScope.launch {
            _selectedLectures.forEach { lectureId ->
                vacancyRepository.removeVacancyLectureNew(lectureId)
                    .onFailure { error ->
                        handleVacancyError(error)
                    }
            }
            loadVacancyLectures()
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
        val vacancyLectures: List<SearchedLecture>,
        val showIntroDialog: Boolean,
        val isEditMode: Boolean,
        val isRefreshing: Boolean,
        val selectedLectures: List<String>,
        val deleteEnabled: Boolean,
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
}

// combine은 기본적으로 5개까지만 지원한다.
fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R,
): Flow<R> = combine(
    combine(flow1, flow2, flow3, ::Triple),
    combine(flow4, flow5, ::Pair),
    combine(flow6, flow7, ::Pair),
) { triple1, pair1, pair2 ->
    transform(
        triple1.first, triple1.second, triple1.third,
        pair1.first, pair1.second,
        pair2.first, pair2.second,
    )
}
