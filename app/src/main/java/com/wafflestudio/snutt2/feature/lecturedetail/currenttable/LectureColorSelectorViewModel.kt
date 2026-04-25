package com.wafflestudio.snutt2.feature.lecturedetail.currenttable

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.domain.model.LectureColor
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.navigation.NavigationDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureColorSelectorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase,
) : ViewModel() {

    private val initialColor: LectureColor =
        checkNotNull(savedStateHandle[NavigationDestination.LectureColorSelector.ARG_COLOR])

    private val _uiState = MutableStateFlow(buildInitialUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LectureColorSelectorUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            getCurrentTableThemeUseCase().collect { tableTheme ->
                _uiState.update {
                    it.copy(
                        tableTheme = tableTheme,
                        isBuiltInTheme = tableTheme !is CustomTheme,
                    )
                }
            }
        }
    }

    private fun buildInitialUiState(): LectureColorSelectorUiState {
        val tableTheme = getCurrentTableThemeUseCase.current()

        val selection = when (initialColor) {
            is LectureColor.BuiltIn -> LectureColorSelectorUiState.Selection.Palette(initialColor.colorIndex)
            is LectureColor.Custom -> when (tableTheme) {
                is CustomTheme -> {
                    // CustomTheme 시간표에선 사용자가 팔레트에서만 색을 고를 수 있으므로 매칭이 항상 성공해야 정상.
                    // 매칭 실패는 테마 갱신 race 등 invariant 위반이지만 화면을 막지 않기 위해 Custom 으로 fallback.
                    val matchedIndex = tableTheme.getColors(false).indexOfFirst {
                        it.foreground == initialColor.foreground && it.background == initialColor.background
                    }
                    if (matchedIndex >= 0) LectureColorSelectorUiState.Selection.Palette(matchedIndex)
                    else LectureColorSelectorUiState.Selection.Custom
                }
                else -> LectureColorSelectorUiState.Selection.Custom
            }
        }
        val customColors = when (initialColor) {
            is LectureColor.Custom -> initialColor
            is LectureColor.BuiltIn -> LectureColor.Custom.Default
        }
        return LectureColorSelectorUiState(
            tableTheme = tableTheme,
            isBuiltInTheme = tableTheme !is CustomTheme,
            selection = selection,
            customFgColor = customColors.foreground,
            customBgColor = customColors.background,
        )
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _uiEvent.emit(LectureColorSelectorUiEvent.NavigateBackWithResult(_uiState.value.selectedColor))
        }
    }

    fun selectPaletteColor(index: Int) {
        _uiState.update { it.copy(selection = LectureColorSelectorUiState.Selection.Palette(index)) }
    }

    fun selectCustom() {
        _uiState.update { it.copy(selection = LectureColorSelectorUiState.Selection.Custom) }
    }

    fun openFgPicker() {
        _uiState.update { it.copy(dialogState = LectureColorSelectorUiState.DialogState.ForegroundPicker(it.customFgColor)) }
    }

    fun openBgPicker() {
        _uiState.update { it.copy(dialogState = LectureColorSelectorUiState.DialogState.BackgroundPicker(it.customBgColor)) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(dialogState = LectureColorSelectorUiState.DialogState.None) }
    }

    fun pickFgColor(argb: Int) {
        _uiState.update {
            it.copy(
                customFgColor = argb,
                selection = LectureColorSelectorUiState.Selection.Custom,
                dialogState = LectureColorSelectorUiState.DialogState.None,
            )
        }
    }

    fun pickBgColor(argb: Int) {
        _uiState.update {
            it.copy(
                customBgColor = argb,
                selection = LectureColorSelectorUiState.Selection.Custom,
                dialogState = LectureColorSelectorUiState.DialogState.None,
            )
        }
    }
}

data class LectureColorSelectorUiState(
    val tableTheme: TableTheme,
    val isBuiltInTheme: Boolean,
    val selection: Selection,
    val customFgColor: Int,
    val customBgColor: Int,
    val dialogState: DialogState = DialogState.None,
) {
    val selectedColor: LectureColor
        get() = when (val s = selection) {
            is Selection.Palette -> {
                if (tableTheme is CustomTheme) {
                    val c = tableTheme.getColors(false)[s.index]
                    LectureColor.Custom(c.foreground, c.background)
                } else {
                    LectureColor.BuiltIn(s.index)
                }
            }

            is Selection.Custom -> LectureColor.Custom(customFgColor, customBgColor)
        }

    sealed interface Selection {
        data class Palette(val index: Int) : Selection
        data object Custom : Selection
    }

    sealed interface DialogState {
        data object None : DialogState
        data class ForegroundPicker(val initialColor: Int) : DialogState
        data class BackgroundPicker(val initialColor: Int) : DialogState
    }
}

sealed interface LectureColorSelectorUiEvent {
    data class NavigateBackWithResult(val selectedColor: LectureColor) : LectureColorSelectorUiEvent
}
