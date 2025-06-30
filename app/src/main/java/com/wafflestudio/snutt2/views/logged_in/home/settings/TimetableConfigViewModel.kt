package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.model.TableLectureCustom
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TableState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimetableConfigViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val currentTableRepository: CurrentTableRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {
    private val _tableTrimParam: StateFlow<TableTrimParam> = userRepository.tableTrimParam
    private val _compactMode: StateFlow<Boolean> = userRepository.compactMode
    private val _tableLectureCustom: StateFlow<TableLectureCustom> = userRepository.tableLectureCustomOption

    private val _timetableConfigUiEvent: MutableSharedFlow<TimetableConfigUiEvent> = MutableSharedFlow(replay = 1)
    val timetableConfigUiEvent = _timetableConfigUiEvent.asSharedFlow()

    fun toggleAutoTrim() {
        viewModelScope.launch {
            userRepository.toggleForceFit()
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    fun setDayOfWeekRange(from: Int, to: Int) {
        viewModelScope.launch {
            userRepository.setDayOfWeekRange(from, to)
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    fun setHourRange(from: Int, to: Int) {
        viewModelScope.launch {
            userRepository.setHourRange(from, to)
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    fun toggleCompactMode() {
        viewModelScope.launch {
            userRepository.toggleCompactMode()
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    fun toggleTitleVisible() {
        viewModelScope.launch {
            userRepository.toggleTitleVisible()
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    fun togglePlaceVisible() {
        viewModelScope.launch {
            userRepository.togglePlaceVisible()
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    fun toggleLectureNumberVisible() {
        viewModelScope.launch {
            userRepository.toggleLectureNumberVisible()
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    fun toggleInstructorVisible() {
        viewModelScope.launch {
            userRepository.toggleInstructorVisible()
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    val timeTableConfigUiState = combine(
        _tableTrimParam, _compactMode, _tableLectureCustom, currentTableRepository.currentTable,
    ) { tableTrimParam, compactMode, tableLectureCustom, currentTable ->
        TimeTableConfigUiState(
            tableTrimParam,
            compactMode,
            tableLectureCustom,
            TableState(
                currentTable ?: TableDto.Default,
                tableTrimParam,
                tableLectureCustom,
                null,
            ),
        )
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, TimeTableConfigUiState.Default)

    private suspend fun handleTimetableConfigError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _timetableConfigUiEvent.emit(TimetableConfigUiEvent.ShowToast(displayMessage))
                userRepository.performLogout()
                _timetableConfigUiEvent.emit(TimetableConfigUiEvent.NavigateToOnboard)
            }
            else -> {
                _timetableConfigUiEvent.emit(TimetableConfigUiEvent.ShowToast(displayMessage))
            }
        }
    }
}

data class TimeTableConfigUiState(
    val tableTrimParam: TableTrimParam,
    val compactMode: Boolean,
    val tableLectureCustom: TableLectureCustom,
    val tableState: TableState,
) {
    companion object {
        val Default = TimeTableConfigUiState(
            TableTrimParam.Default,
            false,
            TableLectureCustom.Default,
            TableState.Default,
        )
    }
}

sealed interface TimetableConfigUiEvent {
    data class ShowToast(val message: String) : TimetableConfigUiEvent
    data object NavigateToOnboard : TimetableConfigUiEvent
}
