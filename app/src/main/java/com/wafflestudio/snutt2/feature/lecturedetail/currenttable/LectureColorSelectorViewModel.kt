package com.wafflestudio.snutt2.feature.lecturedetail.currenttable

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
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

    private fun buildInitialUiState(): LectureColorSelectorUiState = when (val tableTheme = getCurrentTableThemeUseCase.current()) {
        is BuiltInTheme -> buildBuiltInMode(tableTheme)
        is CustomTheme -> buildCustomMode(tableTheme)
    }

    private fun buildBuiltInMode(tableTheme: BuiltInTheme): LectureColorSelectorUiState.BuiltInThemeMode {
        val selection = when (initialColor) {
            is LectureColor.BuiltIn -> LectureColorSelectorUiState.ColorSelection.Palette(initialColor.colorIndex)
            is LectureColor.Custom -> LectureColorSelectorUiState.ColorSelection.Picker
        }
        // selection 이 Custom 일 땐 현재 색, Palette 일 땐 picker 다이얼로그를 처음 열 때 보일 초기값.
        val initialPickerColors = when (initialColor) {
            is LectureColor.Custom -> initialColor
            is LectureColor.BuiltIn -> LectureColor.Custom.Default
        }
        return LectureColorSelectorUiState.BuiltInThemeMode(
            tableTheme = tableTheme,
            selection = selection,
            pickerFgColor = initialPickerColors.foreground,
            pickerBgColor = initialPickerColors.background,
        )
    }

    private fun buildCustomMode(tableTheme: CustomTheme): LectureColorSelectorUiState.CustomThemeMode {
        // CustomTheme 시간표에선 사용자가 팔레트에서만 색을 고를 수 있으므로 매칭이 항상 성공해야 정상.
        // 매칭 실패는 테마 갱신 race 등 invariant 위반이지만 화면을 막지 않기 위해 0번 색으로 fallback.
        val matchedIndex = (initialColor as? LectureColor.Custom)
            ?.let { tableTheme.findPaletteIndex(it) }
            ?: 0
        return LectureColorSelectorUiState.CustomThemeMode(
            tableTheme = tableTheme,
            selection = LectureColorSelectorUiState.ColorSelection.Palette(matchedIndex),
        )
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _uiEvent.emit(LectureColorSelectorUiEvent.NavigateBackWithResult(_uiState.value.selectedColor))
        }
    }

    fun selectPaletteColor(index: Int) {
        _uiState.update {
            when (it) {
                is LectureColorSelectorUiState.BuiltInThemeMode ->
                    it.copy(selection = LectureColorSelectorUiState.ColorSelection.Palette(index))

                is LectureColorSelectorUiState.CustomThemeMode ->
                    it.copy(selection = LectureColorSelectorUiState.ColorSelection.Palette(index))
            }
        }
    }

    fun selectPickerColor() = updateBuiltIn {
        it.copy(selection = LectureColorSelectorUiState.ColorSelection.Picker)
    }

    fun openFgPicker() = updateBuiltIn {
        it.copy(dialogState = LectureColorSelectorUiState.DialogState.ForegroundPicker(it.pickerFgColor))
    }

    fun openBgPicker() = updateBuiltIn {
        it.copy(dialogState = LectureColorSelectorUiState.DialogState.BackgroundPicker(it.pickerBgColor))
    }

    fun dismissDialog() = updateBuiltIn {
        it.copy(dialogState = LectureColorSelectorUiState.DialogState.None)
    }

    fun pickFgColor(argb: Int) = updateBuiltIn {
        it.copy(
            pickerFgColor = argb,
            selection = LectureColorSelectorUiState.ColorSelection.Picker,
            dialogState = LectureColorSelectorUiState.DialogState.None,
        )
    }

    fun pickBgColor(argb: Int) = updateBuiltIn {
        it.copy(
            pickerBgColor = argb,
            selection = LectureColorSelectorUiState.ColorSelection.Picker,
            dialogState = LectureColorSelectorUiState.DialogState.None,
        )
    }

    // BuiltInThemeMode 전제 액션을 위한 helper. 다른 모드일 땐 no-op.
    private inline fun updateBuiltIn(
        transform: (LectureColorSelectorUiState.BuiltInThemeMode) -> LectureColorSelectorUiState.BuiltInThemeMode,
    ) {
        _uiState.update { current ->
            if (current is LectureColorSelectorUiState.BuiltInThemeMode) transform(current) else current
        }
    }
}

sealed interface LectureColorSelectorUiState {
    val tableTheme: TableTheme
    val selection: ColorSelection
    val selectedColor: LectureColor

    data class BuiltInThemeMode(
        override val tableTheme: BuiltInTheme,
        override val selection: ColorSelection,
        val pickerFgColor: Int,
        val pickerBgColor: Int,
        val dialogState: DialogState = DialogState.None,
    ) : LectureColorSelectorUiState {
        override val selectedColor: LectureColor
            get() = when (selection) {
                is ColorSelection.Palette -> LectureColor.BuiltIn(selection.index)
                is ColorSelection.Picker -> LectureColor.Custom(pickerFgColor, pickerBgColor)
            }
    }

    data class CustomThemeMode(
        override val tableTheme: CustomTheme,
        override val selection: ColorSelection.Palette,
    ) : LectureColorSelectorUiState {
        override val selectedColor: LectureColor
            get() {
                val c = tableTheme.getColors(false)[selection.index]
                return LectureColor.Custom(c.foreground, c.background)
            }
    }

    sealed interface ColorSelection {
        data class Palette(val index: Int) : ColorSelection
        data object Picker : ColorSelection
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
