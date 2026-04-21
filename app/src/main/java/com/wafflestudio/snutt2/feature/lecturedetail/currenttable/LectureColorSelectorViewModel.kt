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

    private val initialColor: LectureColor =
        checkNotNull(savedStateHandle[NavigationDestination.LectureColorSelector.ARG_COLOR])

    private val _uiState = MutableStateFlow(LectureColorSelectorUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCurrentTableThemeUseCase().collect { tableTheme ->
                _uiState.update { state ->
                    val newContentState = when (val content = state.contentState) {
                        is LectureColorSelectorUiState.ContentState.Loading -> {
                            val isBuiltIn = tableTheme !is CustomTheme
                            val selectedIndex = when (initialColor) {
                                is LectureColor.BuiltIn -> initialColor.colorIndex
                                is LectureColor.Custom -> tableTheme.getColors(false).indexOfFirst {
                                    it.foreground == initialColor.foreground && it.background == initialColor.background
                                }
                            }
                            val customColors = when (initialColor) {
                                is LectureColor.Custom -> initialColor
                                is LectureColor.BuiltIn -> LectureColor.Custom.Default
                            }
                            LectureColorSelectorUiState.ContentState.Loaded(
                                tableTheme = tableTheme,
                                isBuiltInTheme = isBuiltIn,
                                selectedIndex = selectedIndex,
                                customFgColor = customColors.foreground,
                                customBgColor = customColors.background,
                            )
                        }

                        is LectureColorSelectorUiState.ContentState.Loaded -> content.copy(
                            tableTheme = tableTheme,
                            isBuiltInTheme = tableTheme !is CustomTheme,
                        )
                    }
                    state.copy(contentState = newContentState)
                }
            }
        }
    }

    fun selectPaletteColor(index: Int) {
        _uiState.update { state ->
            val loaded = state.contentState as? LectureColorSelectorUiState.ContentState.Loaded ?: return@update state
            state.copy(contentState = loaded.copy(selectedIndex = index))
        }
    }

    fun selectCustom() {
        _uiState.update { state ->
            val loaded = state.contentState as? LectureColorSelectorUiState.ContentState.Loaded ?: return@update state
            state.copy(contentState = loaded.copy(selectedIndex = -1))
        }
    }

    fun openFgPicker() {
        _uiState.update { state ->
            val loaded = state.contentState as? LectureColorSelectorUiState.ContentState.Loaded ?: return@update state
            state.copy(dialogState = LectureColorSelectorUiState.DialogState.ForegroundPicker(loaded.customFgColor))
        }
    }

    fun openBgPicker() {
        _uiState.update { state ->
            val loaded = state.contentState as? LectureColorSelectorUiState.ContentState.Loaded ?: return@update state
            state.copy(dialogState = LectureColorSelectorUiState.DialogState.BackgroundPicker(loaded.customBgColor))
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(dialogState = LectureColorSelectorUiState.DialogState.None) }
    }

    fun pickFgColor(argb: Int) {
        _uiState.update { state ->
            val loaded = state.contentState as? LectureColorSelectorUiState.ContentState.Loaded ?: return@update state
            state.copy(
                contentState = loaded.copy(
                    customFgColor = argb,
                    selectedIndex = -1,
                ),
                dialogState = LectureColorSelectorUiState.DialogState.None,
            )
        }
    }

    fun pickBgColor(argb: Int) {
        _uiState.update { state ->
            val loaded = state.contentState as? LectureColorSelectorUiState.ContentState.Loaded ?: return@update state
            state.copy(
                contentState = loaded.copy(
                    customBgColor = argb,
                    selectedIndex = -1,
                ),
                dialogState = LectureColorSelectorUiState.DialogState.None,
            )
        }
    }
}

data class LectureColorSelectorUiState(
    val contentState: ContentState = ContentState.Loading,
    val dialogState: DialogState = DialogState.None,
) {
    sealed interface ContentState {
        data object Loading : ContentState

        data class Loaded(
            val tableTheme: TableTheme,
            val isBuiltInTheme: Boolean,
            val selectedIndex: Int,
            val customFgColor: Int,
            val customBgColor: Int,
        ) : ContentState {
            val selectedColor: LectureColor
                get() = when {
                    selectedIndex == -1 -> LectureColor.Custom(customFgColor, customBgColor)
                    tableTheme is CustomTheme -> {
                        val c = tableTheme.getColors(false)[selectedIndex]
                        LectureColor.Custom(c.foreground, c.background)
                    }

                    else -> LectureColor.BuiltIn(selectedIndex)
                }
        }
    }

    sealed interface DialogState {
        data object None : DialogState
        data class ForegroundPicker(val initialColor: Int) : DialogState
        data class BackgroundPicker(val initialColor: Int) : DialogState
    }
}
