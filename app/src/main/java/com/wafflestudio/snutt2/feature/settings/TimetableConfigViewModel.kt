package com.wafflestudio.snutt2.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.table_display.TableDisplayRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.ui.util.getFittingTrimParam
import com.wafflestudio.snutt2.domain.AuthError
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.data.onFailure
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimetableConfigViewModel @Inject constructor(
    private val tableDisplayRepository: TableDisplayRepository,
    private val userRepository: UserRepository,
    private val tableRepository: TableRepository,
    private val getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TimeTableConfigUiState.Default)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<TimetableConfigUiEvent> = MutableSharedFlow(replay = 0)
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                tableDisplayRepository.tableTrimParam,
                tableDisplayRepository.compactMode,
                tableDisplayRepository.tableLectureCustomOption,
                tableRepository.currentTable.filterNotNull(),
                getCurrentTableThemeUseCase(),
            ) { tableTrimParam, compactMode, tableLectureCustom, currentTable, theme ->
                _uiState.update {
                    it.copy(
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
            }.collect()
        }
    }

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

    private suspend fun handleTimetableConfigError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _uiEvent.emit(TimetableConfigUiEvent.ShowToast(displayMessage))
                userRepository.performLogout()
                _uiEvent.emit(TimetableConfigUiEvent.NavigateToOnboard)
            }

            else -> {
                _uiEvent.emit(TimetableConfigUiEvent.ShowToast(displayMessage))
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
