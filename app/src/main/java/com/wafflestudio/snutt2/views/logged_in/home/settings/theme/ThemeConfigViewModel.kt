package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.domain.model.ThemeReference
import com.wafflestudio.snutt2.domain.AuthError
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeConfigViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val tableRepository: TableRepository,
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<ThemeConfigUiEvent>(replay = 0)
    val uiEvent: SharedFlow<ThemeConfigUiEvent> = _uiEvent.asSharedFlow()

    private val _uiState: MutableStateFlow<ThemeConfigUiState> = MutableStateFlow(ThemeConfigUiState())
    val uiState: StateFlow<ThemeConfigUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            themeRepository.fetchThemes()
        }

        viewModelScope.launch {
            combine(
                themeRepository.customThemes,
                themeRepository.builtInThemes,
            ) { customThemes, builtInThemes ->
                _uiState.update { current ->
                    current.copy(
                        myCustomThemes = customThemes.filter { !it.isFromMarket },
                        marketCustomThemes = customThemes.filter { it.isFromMarket },
                        builtInThemes = builtInThemes,
                    )
                }
            }.collect()
        }
    }

    fun onOpenBottomSheet(theme: TableTheme) {
        viewModelScope.launch {
            _uiState.update { current ->
                current.copy(
                    bottomSheetType = when (theme) {
                        is CustomTheme -> if (theme.isFromMarket) {
                            ThemeConfigUiState.BottomSheetType.MarketCustomThemeActions(theme)
                        } else {
                            ThemeConfigUiState.BottomSheetType.MyCustomThemeActions(theme)
                        }

                        is BuiltInTheme -> ThemeConfigUiState.BottomSheetType.BuiltInThemeActions(theme)
                    },
                )
            }
            _uiEvent.emit(ThemeConfigUiEvent.OpenBottomSheet)
        }
    }

    fun onCloseBottomSheet() {
        viewModelScope.launch {
            _uiEvent.emit(ThemeConfigUiEvent.CloseBottomSheet)
        }
    }

    fun onSheetDismissed() {
        _uiState.update { current ->
            current.copy(bottomSheetType = ThemeConfigUiState.BottomSheetType.None)
        }
    }

    fun onClickDetail(theme: TableTheme) {
        viewModelScope.launch {
            _uiEvent.emit(ThemeConfigUiEvent.CloseBottomSheet)
            _uiEvent.emit(ThemeConfigUiEvent.NavigateToDetail(theme))
        }
    }

    fun onClickApply(theme: TableTheme) {
        viewModelScope.launch {
            _uiEvent.emit(ThemeConfigUiEvent.CloseBottomSheet)

            val currentTableId = tableRepository.currentTable.value?.summary?.id ?: return@launch
            when (theme) {
                is CustomTheme -> tableRepository.updateTableTheme(currentTableId, theme.id)
                is BuiltInTheme -> tableRepository.updateTableTheme(currentTableId, theme.code)
            }.onFailure { handleError(it) }
        }
    }

    fun onClickDuplicate(theme: CustomTheme) {
        viewModelScope.launch {
            _uiEvent.emit(ThemeConfigUiEvent.CloseBottomSheet)
            themeRepository.copyTheme(theme.id)
                .onFailure { handleError(it) }
        }
    }

    fun onClickDelete(theme: CustomTheme) {
        _uiState.update { current ->
            current.copy(dialogState = ThemeConfigUiState.DialogState.DeleteTheme(theme))
        }
    }

    fun onConfirmDeleteTheme() {
        val theme = (_uiState.value.dialogState as? ThemeConfigUiState.DialogState.DeleteTheme)?.theme ?: return

        viewModelScope.launch {
            _uiState.update { current ->
                current.copy(dialogState = ThemeConfigUiState.DialogState.None)
            }
            _uiEvent.emit(ThemeConfigUiEvent.CloseBottomSheet)

            themeRepository.deleteTheme(theme.id)
                .onSuccess {
                    val currentTable = tableRepository.currentTable.value ?: return@onSuccess
                    val isApplied = (currentTable.themeRef as? ThemeReference.Custom)?.themeId == theme.id
                    if (isApplied) {
                        tableRepository.fetchAndSelectTable(currentTable.summary.id)
                            .onFailure { error -> handleError(error) }
                    }
                }
                .onFailure { handleError(it) }
        }
    }

    fun onDismissDialog() {
        _uiState.update { current ->
            current.copy(dialogState = ThemeConfigUiState.DialogState.None)
        }
    }

    private suspend fun handleError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _uiEvent.emit(ThemeConfigUiEvent.ShowToast(displayMessage))
                userRepository.postForceLogout()
            }

            else -> {
                _uiEvent.emit(ThemeConfigUiEvent.ShowToast(displayMessage))
            }
        }
    }
}

sealed interface ThemeConfigUiEvent {
    data class ShowToast(val message: String) : ThemeConfigUiEvent
    data object OpenBottomSheet : ThemeConfigUiEvent
    data object CloseBottomSheet : ThemeConfigUiEvent
    data class NavigateToDetail(val theme: TableTheme) : ThemeConfigUiEvent
}

data class ThemeConfigUiState(
    val myCustomThemes: List<CustomTheme> = emptyList(),
    val marketCustomThemes: List<CustomTheme> = emptyList(),
    val builtInThemes: List<BuiltInTheme> = emptyList(),
    val bottomSheetType: BottomSheetType = BottomSheetType.None,
    val dialogState: DialogState = DialogState.None,
) {
    sealed interface BottomSheetType {
        data object None : BottomSheetType
        data class MyCustomThemeActions(val theme: CustomTheme) : BottomSheetType
        data class MarketCustomThemeActions(val theme: CustomTheme) : BottomSheetType
        data class BuiltInThemeActions(val theme: BuiltInTheme) : BottomSheetType
    }

    sealed interface DialogState {
        data object None : DialogState
        data class DeleteTheme(val theme: CustomTheme) : DialogState
    }
}
