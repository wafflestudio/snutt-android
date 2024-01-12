package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.model.TableTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val themeRepository: ThemeRepository,
) : ViewModel() {

    private val _editingTheme = MutableStateFlow<TableTheme>(CustomTheme.New)
    val editingTheme: StateFlow<TableTheme> get() = _editingTheme

    // 색상별 id가 없어 expanded 여부를 설정할 수 없으므로 List<Selectable<ColorDto>>로 따로 관리한다
    private val _editingColors = MutableStateFlow<List<Selectable<ColorDto>>>(emptyList())
    val editingColors: StateFlow<List<Selectable<ColorDto>>> get() = _editingColors

    init {
        val themeId = savedStateHandle.get<String>("themeId")
        val theme = savedStateHandle.get<Int>("theme")
        theme?.let {
            if (theme != -1) {
                _editingTheme.value = themeRepository.getTheme(theme)
            } else {
                themeId?.let {
                    _editingTheme.value = if (themeId.isEmpty()) CustomTheme.New else themeRepository.getTheme(themeId)
                    _editingColors.value = (_editingTheme.value as CustomTheme).colors.map { color ->
                        color.toDataWithState(false)
                    }
                }
            }
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

    fun hasChange(name: String, isDefault: Boolean): Boolean {
        return name != _editingTheme.value.name ||
            isDefault != _editingTheme.value.isDefault ||
            (_editingTheme.value is CustomTheme && _editingColors.value.map { it.item } != (_editingTheme.value as CustomTheme).colors)
    }

    suspend fun saveTheme(name: String) {
        if (_editingTheme.value is CustomTheme) {
            val newTheme = (_editingTheme.value as CustomTheme).id.let { id ->
                if (id.isEmpty()) {
                    themeRepository.createTheme(name, _editingColors.value.map { it.item })
                } else {
                    themeRepository.updateTheme(id, name, _editingColors.value.map { it.item })
                }
            }
            _editingTheme.value = newTheme
        }
    }

    suspend fun setThemeDefault() {
        if (_editingTheme.value is CustomTheme) {
            themeRepository.setCustomThemeDefault((_editingTheme.value as CustomTheme).id)
        } else {
            themeRepository.setBuiltInThemeDefault((_editingTheme.value as BuiltInTheme).code)
        }
    }

    suspend fun unsetThemeDefault() {
        if (_editingTheme.value is CustomTheme) {
            themeRepository.unsetCustomThemeDefault((_editingTheme.value as CustomTheme).id)
        } else {
            themeRepository.setBuiltInThemeDefault(0) // FIXME: DELETE /themes/basic/~/default 나오면 바꾸기
        }
    }
}
