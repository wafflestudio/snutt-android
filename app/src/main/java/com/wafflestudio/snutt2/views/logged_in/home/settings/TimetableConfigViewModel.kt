package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.table_display.TableDisplayRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.lib.getFittingTrimParam
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.onFailure
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimetableConfigViewModel @Inject constructor(
    private val tableDisplayRepository: TableDisplayRepository,
    private val userRepository: UserRepository,
    tableRepository: TableRepository,
    getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {
    private val _timetableConfigUiEvent: MutableSharedFlow<TimetableConfigUiEvent> = MutableSharedFlow(replay = 1)
    val timetableConfigUiEvent = _timetableConfigUiEvent.asSharedFlow()

    fun toggleAutoTrim() {
        viewModelScope.launch {
            tableDisplayRepository.toggleForceFit()
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    fun setDayOfWeekRange(from: Int, to: Int) {
        viewModelScope.launch {
            tableDisplayRepository.setDayOfWeekRange(from, to)
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    fun setHourRange(from: Int, to: Int) {
        viewModelScope.launch {
            tableDisplayRepository.setHourRange(from, to)
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    fun toggleCompactMode() {
        viewModelScope.launch {
            tableDisplayRepository.toggleCompactMode()
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    fun toggleTitleVisible() {
        viewModelScope.launch {
            tableDisplayRepository.toggleTitleVisible()
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    fun togglePlaceVisible() {
        viewModelScope.launch {
            tableDisplayRepository.togglePlaceVisible()
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    fun toggleLectureNumberVisible() {
        viewModelScope.launch {
            tableDisplayRepository.toggleLectureNumberVisible()
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    fun toggleInstructorVisible() {
        viewModelScope.launch {
            tableDisplayRepository.toggleInstructorVisible()
                .onFailure { error ->
                    handleTimetableConfigError(error)
                }
        }
    }

    val timeTableConfigUiState = combine(
        tableDisplayRepository.tableTrimParam,
        tableDisplayRepository.compactMode,
        tableDisplayRepository.tableLectureCustomOption,
        tableRepository.currentTable.filterNotNull(),
        getCurrentTableThemeUseCase(),
    ) { tableTrimParam, compactMode, tableLectureCustom, currentTable, theme ->
        TimeTableConfigUiState(
            tableTrimParam = tableTrimParam,
            compactMode = compactMode,
            tableLectureCustom = tableLectureCustom,
            lectures = currentTable.lectures,
            theme = theme,
            fittedTrimParam = if (tableTrimParam.forceFitLectures) {
                currentTable.lectures.getFittingTrimParam(TableTrimParam.Default)
            } else {
                tableTrimParam
            },
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
    val lectures: List<LocalLecture>,
    val theme: TableTheme,
    val fittedTrimParam: TableTrimParam,
) {
    companion object {
        val Default = TimeTableConfigUiState(
            tableTrimParam = TableTrimParam.Default,
            compactMode = false,
            tableLectureCustom = TableLectureCustom.Default,
            lectures = emptyList(),
            theme = BuiltInTheme.SNUTT,
            fittedTrimParam = TableTrimParam.Default,
        )
    }
}

sealed interface TimetableConfigUiEvent {
    data class ShowToast(val message: String) : TimetableConfigUiEvent
    data object NavigateToOnboard : TimetableConfigUiEvent
}
