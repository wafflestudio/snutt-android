package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.lifecycle.SavedStateHandle
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
    savedStateHandle: SavedStateHandle,
    private val themeRepository: ThemeRepository,
) : ViewModel() {

    private val _editingTheme = MutableStateFlow(ThemeDto.NewCustomTheme)
    val editingTheme: StateFlow<ThemeDto> get() = _editingTheme

    // 색상별 id가 없어 expanded 여부를 설정할 수 없으므로 List<Selectable<ColorDto>>로 따로 관리한다
    private val _themeColors = MutableStateFlow<List<Selectable<ColorDto>>>(emptyList())
    val themeColors: StateFlow<List<Selectable<ColorDto>>> get() = _themeColors

    init {
        val themeId = savedStateHandle.get<Long>("themeId")
        val theme = savedStateHandle.get<Int>("theme")
        viewModelScope.launch {
            theme?.let {
                if (theme != -1) {
                    _editingTheme.value = ThemeDto.builtInThemeFromCode(theme)
                } else {
                    themeId?.let {
                        _editingTheme.value = if (themeId == 0L) ThemeDto.NewCustomTheme else themeRepository.getTheme(themeId)
                    }
                }
            }
            _themeColors.value = _editingTheme.value.colors.map { color ->
                color.toDataWithState(false)
            }
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

    fun hasChange(name: String, isDefault: Boolean): Boolean {
        return name != _editingTheme.value.name || isDefault != _editingTheme.value.isDefault || _themeColors.value.map { it.item } != _editingTheme.value.colors
    }

    suspend fun saveTheme(name: String, isDefault: Boolean) {
        val newTheme = _editingTheme.value.copy(
            name = name,
            isDefault = isDefault,
            colors = _themeColors.value.map { it.item },
        ).let {
            if (_editingTheme.value.id == 0L) {
                themeRepository.createTheme(it)
            } else {
                themeRepository.updateTheme(it)
            }
        }
        _editingTheme.value = newTheme
    }

    suspend fun setAsDefaultTheme() {
        if (_editingTheme.value.isCustom) {
            themeRepository.setDefaultTheme(_editingTheme.value.id ?: 0L)
        } else {
            themeRepository.setDefaultTheme(_editingTheme.value.code)
        }
    }

    suspend fun unsetDefaultTheme() {
        themeRepository.setDefaultTheme(0)
    }
}
