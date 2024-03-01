package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.network.ApiOnError
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
    private val tableRepository: TableRepository,
    private val currentTableRepository: CurrentTableRepository,
    private val apiOnError: ApiOnError,
) : ViewModel() {

    private val _editingTheme = MutableStateFlow<TableTheme>(CustomTheme.Default)
    val editingTheme: StateFlow<TableTheme> get() = _editingTheme

    // 색상별 id가 없어 expanded 여부를 설정할 수 없으므로 List<Selectable<ColorDto>>로 따로 관리한다
    private val _editingColors = MutableStateFlow<List<Selectable<ColorDto>>>(emptyList())
    val editingColors: StateFlow<List<Selectable<ColorDto>>> get() = _editingColors

    val currentTable = currentTableRepository.currentTable

    var isNewTheme = false

    init {
        val themeId = savedStateHandle.get<String>("themeId")
        val theme = savedStateHandle.get<Int>("theme")
        if (theme != null && themeId != null) {
            if (theme != -1) {
                try {
                    _editingTheme.value = themeRepository.getTheme(theme)
                } catch (e: Exception) {
                    apiOnError(e)
                }
            } else {
                _editingTheme.value = if (themeId.isEmpty()) {
                    isNewTheme = true
                    CustomTheme.Default
                } else {
                    try {
                        themeRepository.getTheme(themeId)
                    } catch (e: Exception) {
                        apiOnError(e)
                        CustomTheme.Default
                    }
                }
                _editingColors.value =
                    (_editingTheme.value as CustomTheme).colors.mapIndexed { idx, color ->
                        color.toDataWithState(idx == 0)
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
            _editingTheme.value = (_editingTheme.value as CustomTheme).id.let { id ->
                if (id.isEmpty()) {
                    themeRepository.createTheme(name, _editingColors.value.map { it.item })
                } else {
                    themeRepository.updateTheme(id, name, _editingColors.value.map { it.item })
                }
            }
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
            themeRepository.unsetBuiltInThemeDefault((_editingTheme.value as BuiltInTheme).code)
        }
    }

    suspend fun applyThemeToCurrentTable() {
        currentTable.value?.let { table ->
            when (_editingTheme.value) {
                is CustomTheme -> {
                    tableRepository.updateTableTheme(
                        table.id,
                        (_editingTheme.value as CustomTheme).id,
                    )
                }

                is BuiltInTheme -> {
                    tableRepository.updateTableTheme(
                        table.id,
                        (_editingTheme.value as BuiltInTheme).code,
                    )
                }
            }
        }
    }

    suspend fun refreshCurrentTableIfNeeded() { // 현재 선택된 시간표의 테마라면 새로고침
        currentTable.value?.let {
            if (it.themeId != null && it.themeId == (_editingTheme.value as? CustomTheme)?.id) {
                tableRepository.fetchTableById(it.id)
            }
        }
    }
}
