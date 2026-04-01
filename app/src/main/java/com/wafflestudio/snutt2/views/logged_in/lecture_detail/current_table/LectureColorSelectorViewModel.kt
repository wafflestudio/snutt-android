package com.wafflestudio.snutt2.views.logged_in.lecture_detail.current_table

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domainmodel.CustomTheme
import com.wafflestudio.snutt2.domainmodel.LectureColor
import com.wafflestudio.snutt2.domainmodel.TableTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureColorSelectorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase,
) : ViewModel() {

    private val initialColorIndex: Int = savedStateHandle["colorIndex"] ?: -1
    private val initialFgColor: Int = savedStateHandle["fgColor"] ?: 0
    private val initialBgColor: Int = savedStateHandle["bgColor"] ?: 0

    private val _uiState = MutableStateFlow<LectureColorSelectorUiState>(LectureColorSelectorUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCurrentTableThemeUseCase().collect { tableTheme ->
                _uiState.update { state ->
                    when (state) {
                        is LectureColorSelectorUiState.Loading -> {
                            val isBuiltIn = tableTheme !is CustomTheme
                            val selectedIndex = if (tableTheme is CustomTheme) {
                                tableTheme.getColors(false).indexOfFirst {
                                    it.foreground == initialFgColor && it.background == initialBgColor
                                }
                            } else {
                                initialColorIndex
                            }
                            LectureColorSelectorUiState.Loaded(
                                tableTheme = tableTheme,
                                isBuiltInTheme = isBuiltIn,
                                selectedIndex = selectedIndex,
                                customFgColor = initialFgColor,
                                customBgColor = initialBgColor,
                            )
                        }

                        is LectureColorSelectorUiState.Loaded -> state.copy(
                            tableTheme = tableTheme,
                            isBuiltInTheme = tableTheme !is CustomTheme,
                        )
                    }
                }
            }
        }
    }

    fun selectPaletteColor(index: Int) {
        updateLoaded { copy(selectedIndex = index) }
    }

    fun selectCustom() {
        updateLoaded { copy(selectedIndex = -1) }
    }

    fun openFgPicker() {
        updateLoaded {
            copy(
                dialogState = LectureColorSelectorUiState.DialogState.ForegroundPicker(customFgColor),
            )
        }
    }

    fun openBgPicker() {
        updateLoaded {
            copy(
                dialogState = LectureColorSelectorUiState.DialogState.BackgroundPicker(customBgColor),
            )
        }
    }

    fun dismissDialog() {
        updateLoaded { copy(dialogState = LectureColorSelectorUiState.DialogState.None) }
    }

    fun pickFgColor(argb: Int) {
        updateLoaded {
            copy(
                customFgColor = argb,
                selectedIndex = -1,
                dialogState = LectureColorSelectorUiState.DialogState.None,
            )
        }
    }

    fun pickBgColor(argb: Int) {
        updateLoaded {
            copy(
                customBgColor = argb,
                selectedIndex = -1,
                dialogState = LectureColorSelectorUiState.DialogState.None,
            )
        }
    }

    fun getSelectedColor(): LectureColor {
        val state = _uiState.value as? LectureColorSelectorUiState.Loaded
            ?: return LectureColor.BuiltIn(0)

        return when {
            state.selectedIndex == -1 -> LectureColor.Custom(state.customFgColor, state.customBgColor)
            state.tableTheme is CustomTheme -> {
                val c = state.tableTheme.getColors(false)[state.selectedIndex]
                LectureColor.Custom(c.foreground, c.background)
            }

            else -> LectureColor.BuiltIn(state.selectedIndex)
        }
    }

    private fun updateLoaded(
        transform: LectureColorSelectorUiState.Loaded.() -> LectureColorSelectorUiState.Loaded,
    ) {
        _uiState.update { state ->
            when (state) {
                is LectureColorSelectorUiState.Loaded -> state.transform()
                else -> state
            }
        }
    }

}

sealed interface LectureColorSelectorUiState {
    data object Loading : LectureColorSelectorUiState

    data class Loaded(
        val tableTheme: TableTheme,
        val isBuiltInTheme: Boolean,
        val selectedIndex: Int,
        val customFgColor: Int,
        val customBgColor: Int,
        val dialogState: DialogState = DialogState.None,
    ) : LectureColorSelectorUiState

    sealed interface DialogState {
        data object None : DialogState
        data class ForegroundPicker(val initialColor: Int) : DialogState
        data class BackgroundPicker(val initialColor: Int) : DialogState
    }
}
