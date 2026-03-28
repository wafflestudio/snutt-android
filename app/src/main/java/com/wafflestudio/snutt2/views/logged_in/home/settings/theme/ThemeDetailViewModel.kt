package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.wafflestudio.snutt2.data.table_display.TableDisplayRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CustomTheme
import com.wafflestudio.snutt2.domainmodel.EditingTheme
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.domainmodel.ThemeColor
import com.wafflestudio.snutt2.domainmodel.ThemeReference
import com.wafflestudio.snutt2.lib.getFittingTrimParam
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.views.NavigationDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val themeRepository: ThemeRepository,
    private val tableRepository: TableRepository,
    private val tableDisplayRepository: TableDisplayRepository,
    private val userRepository: UserRepository,
    private val getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<ThemeDetailUiEvent>(replay = 0)
    val uiEvent: SharedFlow<ThemeDetailUiEvent> = _uiEvent.asSharedFlow()

    private val _uiState = MutableStateFlow<ThemeDetailUiState>(ThemeDetailUiState.Loading)
    val uiState: StateFlow<ThemeDetailUiState> = _uiState.asStateFlow()

    init {
        val initialEditingTheme = computeInitialEditingTheme()

        viewModelScope.launch {
            combine(
                tableRepository.currentTable.filterNotNull(),
                getCurrentTableThemeUseCase(),
                combine(
                    tableDisplayRepository.tableTrimParam,
                    tableDisplayRepository.tableLectureCustomOption,
                    tableDisplayRepository.compactMode,
                    ::Triple,
                ),
            ) { table, theme, (trimParam, lectureCustomOption, compactMode) ->
                _uiState.update { current ->
                    val editingTheme = (current as? ThemeDetailUiState.Success)?.editingTheme
                        ?: initialEditingTheme
                        ?: return@update ThemeDetailUiState.Error

                    val prev = current as? ThemeDetailUiState.Success
                    val fittedTrimParam = if (trimParam.forceFitLectures) {
                        table.lectures.getFittingTrimParam(TableTrimParam.Default)
                    } else {
                        trimParam
                    }

                    ThemeDetailUiState.Success(
                        editingTheme = editingTheme,
                        lectures = table.lectures,
                        theme = theme,
                        previewTheme = editingTheme.toTableTheme(),
                        fittedTrimParam = fittedTrimParam,
                        tableLectureCustomOptions = lectureCustomOption,
                        compactMode = compactMode,
                        dialogState = prev?.dialogState ?: ThemeDetailUiState.DialogState.None,
                    )
                }
            }.collect()
        }
    }

    private fun computeInitialEditingTheme(): EditingTheme? {
        val themeId = savedStateHandle.toRoute<NavigationDestination.ThemeDetail>().themeId
        val themeCode = savedStateHandle.toRoute<NavigationDestination.ThemeDetail>().theme

        return if (themeCode != -1) {
            try {
                EditingTheme.fromTableTheme(BuiltInTheme.fromCode(themeCode))
            } catch (_: Exception) {
                null
            }
        } else {
            val originalTheme = if (themeId.isEmpty()) CustomTheme.Default else themeRepository.getTheme(themeId)
            EditingTheme.fromTableTheme(originalTheme)
        }
    }

    private inline fun updateEditingTheme(crossinline transform: (EditingTheme) -> EditingTheme) {
        _uiState.update { current ->
            val success = current as? ThemeDetailUiState.Success ?: return@update current
            if (!success.editingTheme.isEditable) return@update current
            val newEditingTheme = transform(success.editingTheme)
            success.copy(
                editingTheme = newEditingTheme,
                previewTheme = newEditingTheme.toTableTheme(),
            )
        }
    }

    fun addColor() {
        updateEditingTheme { theme ->
            val newColors = theme.colors.toMutableList().apply {
                add(CustomTheme.Default.getColors().first().toDataWithState(true))
            }
            theme.copy(colors = newColors)
        }
    }

    fun removeColor(index: Int) {
        updateEditingTheme { theme ->
            theme.copy(colors = theme.colors.toMutableList().apply { removeAt(index) })
        }
    }

    fun updateColor(index: Int, fgColor: Int, bgColor: Int) {
        updateEditingTheme { theme ->
            val newColors = theme.colors.toMutableList().apply {
                set(index, ThemeColor(fgColor, bgColor).toDataWithState(get(index).state))
            }
            theme.copy(colors = newColors)
        }
    }

    fun duplicateColor(index: Int) {
        updateEditingTheme { theme ->
            val newColors = theme.colors.toMutableList().apply {
                add(index + 1, get(index).copy(state = false))
            }
            theme.copy(colors = newColors)
        }
    }

    fun toggleColorExpanded(index: Int) {
        updateEditingTheme { theme ->
            val newColors = theme.colors.toMutableList().apply {
                set(index, get(index).run { copy(state = !state) })
            }
            theme.copy(colors = newColors)
        }
    }

    fun updateName(name: String) {
        updateEditingTheme { theme -> theme.copy(name = name) }
    }

    fun onClickBack() {
        val success = _uiState.value as? ThemeDetailUiState.Success ?: return
        if (success.editingTheme.hasChange()) {
            _uiState.update { current ->
                if (current !is ThemeDetailUiState.Success) return@update current
                current.copy(dialogState = ThemeDetailUiState.DialogState.ConfirmCancelEdit)
            }
        } else {
            viewModelScope.launch { _uiEvent.emit(ThemeDetailUiEvent.NavigateBack) }
        }
    }

    fun onConfirmCancelEdit() {
        _uiState.update { current ->
            if (current !is ThemeDetailUiState.Success) return@update current
            current.copy(dialogState = ThemeDetailUiState.DialogState.None)
        }
        viewModelScope.launch { _uiEvent.emit(ThemeDetailUiEvent.NavigateBack) }
    }

    fun onDismissCancelEdit() {
        _uiState.update { current ->
            if (current !is ThemeDetailUiState.Success) return@update current
            current.copy(dialogState = ThemeDetailUiState.DialogState.None)
        }
    }

    fun onSaveTheme() {
        viewModelScope.launch {
            val success = _uiState.value as? ThemeDetailUiState.Success ?: return@launch
            val theme = success.editingTheme.toTableTheme() as? CustomTheme ?: return@launch
            if (!theme.isEditable) return@launch

            val isNew = theme.isNew
            val colors = theme.getColors()
            val result = if (isNew) {
                themeRepository.createTheme(theme.name, colors)
            } else {
                themeRepository.updateTheme(theme.id, theme.name, colors)
            }

            result.onSuccess { newTheme ->
                val newEditingTheme = EditingTheme.fromTableTheme(newTheme)
                _uiState.update { current ->
                    val s = current as? ThemeDetailUiState.Success ?: return@update current
                    s.copy(
                        editingTheme = newEditingTheme,
                        previewTheme = newEditingTheme.toTableTheme(),
                        dialogState = if (isNew) {
                            ThemeDetailUiState.DialogState.ConfirmApplyToTable
                        } else {
                            s.dialogState
                        },
                    )
                }
                if (!isNew) {
                    val currentTable = tableRepository.currentTable.value
                    if (currentTable != null &&
                        (currentTable.themeRef as? ThemeReference.Custom)?.themeId == newTheme.id
                    ) {
                        tableRepository.fetchAndSelectTable(currentTable.summary.id)
                            .onFailure { handleError(it) }
                    }
                    _uiEvent.emit(ThemeDetailUiEvent.NavigateBack)
                }
            }.onFailure { handleError(it) }
        }
    }

    fun onConfirmApplyToTable() {
        viewModelScope.launch {
            val currentTable = tableRepository.currentTable.value
            val theme = (_uiState.value as? ThemeDetailUiState.Success)
                ?.editingTheme?.toTableTheme() as? CustomTheme
            if (currentTable != null && theme != null) {
                tableRepository.updateTableTheme(currentTable.summary.id, theme.id)
                    .onFailure { handleError(it) }
            }
            _uiState.update { current ->
                if (current !is ThemeDetailUiState.Success) return@update current
                current.copy(dialogState = ThemeDetailUiState.DialogState.None)
            }
            _uiEvent.emit(ThemeDetailUiEvent.NavigateBack)
        }
    }

    fun onDismissApplyToTable() {
        _uiState.update { current ->
            if (current !is ThemeDetailUiState.Success) return@update current
            current.copy(dialogState = ThemeDetailUiState.DialogState.None)
        }
        viewModelScope.launch { _uiEvent.emit(ThemeDetailUiEvent.NavigateBack) }
    }

    private suspend fun handleError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _uiEvent.emit(ThemeDetailUiEvent.ShowToast(displayMessage))
                userRepository.postForceLogout()
            }

            else -> {
                _uiEvent.emit(ThemeDetailUiEvent.ShowToast(displayMessage))
            }
        }
    }
}

sealed interface ThemeDetailUiEvent {
    data class ShowToast(val message: String) : ThemeDetailUiEvent
    data object NavigateBack : ThemeDetailUiEvent
}

sealed interface ThemeDetailUiState {
    data object Loading : ThemeDetailUiState
    data object Error : ThemeDetailUiState

    data class Success(
        val editingTheme: EditingTheme,
        val lectures: List<LocalLecture>,
        val theme: TableTheme,
        val previewTheme: TableTheme?,
        val fittedTrimParam: TableTrimParam,
        val tableLectureCustomOptions: TableLectureCustom,
        val compactMode: Boolean,
        val dialogState: DialogState = DialogState.None,
    ) : ThemeDetailUiState

    sealed interface DialogState {
        data object None : DialogState
        data object ConfirmCancelEdit : DialogState
        data object ConfirmApplyToTable : DialogState
    }
}
