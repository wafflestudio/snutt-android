package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.ThemeDto
import com.wafflestudio.snutt2.lib.toDataWithState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeDetailViewModel @Inject constructor() : ViewModel() {

    private val _editingTheme = MutableStateFlow(ThemeDto())
    val editingTheme: StateFlow<ThemeDto> get() = _editingTheme

    // 색상별 id가 없어 expanded 여부를 설정할 수 없으므로 List<Selectable<ColorDto>>로 따로 관리한다
    private val _editingColors = MutableStateFlow<List<Selectable<ColorDto>>>(emptyList())
    val editingColors: StateFlow<List<Selectable<ColorDto>>> get() = _editingColors

    fun initializeEditingTheme(theme: ThemeDto) {
        _editingTheme.value = theme
        _editingColors.value = theme.colors.map { color ->
            color.toDataWithState(false)
        }
    }

    fun addColor() {
        _editingColors.value = _editingColors.value.toMutableList().apply {
            add(ColorDto(fgColor = 0xffffff, bgColor = 0x1bd0c8).toDataWithState(true))
        }
    }

    fun removeColor(index: Int) {
        _editingColors.value = _editingColors.value.toMutableList().apply {
            removeAt(index)
        }
    }

    fun updateColor(index: Int, fgColor: Int, bgColor: Int) {
        _editingColors.value = _editingColors.value.toMutableList().apply {
            set(index, ColorDto(fgColor, bgColor).toDataWithState(get(index).state))
        }
    }

    fun duplicateColor(index: Int) {
        _editingColors.value = _editingColors.value.toMutableList().apply {
            add(index + 1, get(index).copy(state = false))
        }
    }

    fun toggleColorExpanded(index: Int) {
        _editingColors.value = _editingColors.value.toMutableList().apply {
            set(index, get(index).run { copy(state = !state) })
        }
    }
}
