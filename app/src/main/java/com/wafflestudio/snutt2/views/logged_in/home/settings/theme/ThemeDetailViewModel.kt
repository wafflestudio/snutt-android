package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.ThemeDto
import com.wafflestudio.snutt2.lib.toDataWithState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeDetailViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
) : ViewModel() {

    private val _editingTheme = MutableStateFlow(ThemeDto())
    val editingTheme: StateFlow<ThemeDto> get() = _editingTheme

    // 색상별 id가 없어 expanded 여부를 설정할 수 없으므로 List<Selectable<ColorDto>>로 따로 관리한다
    private val _themeColors = MutableStateFlow<List<Selectable<ColorDto>>>(emptyList())
    val themeColors: StateFlow<List<Selectable<ColorDto>>> get() = _themeColors

    fun initializeCustomTheme(themeId: Long) {
        viewModelScope.launch {
            _editingTheme.value = if (themeId == 0L) ThemeDto.NewCustomTheme else themeRepository.getTheme(themeId)
            _themeColors.value = _editingTheme.value.colors.map { color ->
                color.toDataWithState(false)
            }
        }
    }

    fun initializeBuiltInTheme(theme: Int) {
        _editingTheme.value = ThemeDto.builtInThemeFromInt(theme)
        _themeColors.value = _editingTheme.value.colors.map { color ->
            color.toDataWithState(false)
        }
    }

    fun addColor() {
        _themeColors.value = _themeColors.value.toMutableList().apply {
            add(ColorDto(fgColor = 0xffffff, bgColor = 0x1bd0c8).toDataWithState(true))
        }
    }

    fun removeColor(index: Int) {
        _themeColors.value = _themeColors.value.toMutableList().apply {
            removeAt(index)
        }
    }

    fun updateColor(index: Int, fgColor: Int, bgColor: Int) {
        _themeColors.value = _themeColors.value.toMutableList().apply {
            set(index, ColorDto(fgColor, bgColor).toDataWithState(get(index).state))
        }
    }

    fun duplicateColor(index: Int) {
        _themeColors.value = _themeColors.value.toMutableList().apply {
            add(index + 1, get(index).copy(state = false))
        }
    }

    fun toggleColorExpanded(index: Int) {
        _themeColors.value = _themeColors.value.toMutableList().apply {
            set(index, get(index).run { copy(state = !state) })
        }
    }

    fun updateThemeName(themeName: String) {
        _editingTheme.value = _editingTheme.value.copy(name = themeName)
    }

    fun updateIsDefault(isDefault: Boolean) {
        _editingTheme.value = _editingTheme.value.copy(isDefault = isDefault)
    }

    suspend fun createCustomTheme() {
        _editingTheme.value = _editingTheme.value.copy(colors = _themeColors.value.map { it.item })
        themeRepository.createTheme(_editingTheme.value)
    }

    suspend fun updateCustomTheme() {
        _editingTheme.value = _editingTheme.value.copy(colors = _themeColors.value.map { it.item })
        themeRepository.updateTheme(_editingTheme.value)
    }
}
