package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import android.graphics.Insets.add
import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.CustomThemeDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeDetailViewModel @Inject constructor() : ViewModel() {

    private val _editingTheme = MutableStateFlow(CustomThemeDto())
    val editingTheme: StateFlow<CustomThemeDto> get() = _editingTheme

    /*val editingThemeColors: StateFlow<List<Selectable<ColorDto>>> get() = _editingTheme.map {
        it.colors.map { color ->
            color.toDataWithState(false)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())*/

    fun initializeEditingTheme(theme: CustomThemeDto) {
        _editingTheme.value = theme
    }

    fun addColor() {
        _editingTheme.value = _editingTheme.value.copy(
            colors = _editingTheme.value.colors.toMutableList().apply {
                add(ColorDto(fgColor = 0xffffff, bgColor = 0x1BD0C8))
            },
        )
    }

    fun removeColor(index: Int) {
        _editingTheme.value = _editingTheme.value.copy(
            colors = _editingTheme.value.colors.toMutableList().apply {
                removeAt(index)
            },
        )
    }

    fun updateColor(index: Int, fgColor: Int, bgColor: Int) {
        _editingTheme.value = _editingTheme.value.copy(
            colors = _editingTheme.value.colors.toMutableList().apply {
                set(index, ColorDto(fgColor, bgColor))
            },
        )
    }

    fun duplicateColor(index: Int) {
        _editingTheme.value = _editingTheme.value.copy(
            colors = _editingTheme.value.colors.toMutableList().apply {
                add(index + 1, get(index))
            },
        )
    }
}
