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
import com.wafflestudio.snutt2.model.BuiltInTheme1
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.model.CustomTheme1
import com.wafflestudio.snutt2.model.EditingTheme
import com.wafflestudio.snutt2.model.TableTheme1
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val themeRepository: ThemeRepository,
    private val tableRepository: TableRepository,
    currentTableRepository: CurrentTableRepository,
    private val apiOnError: ApiOnError,
) : ViewModel() {

    private val _editingTheme = MutableStateFlow<EditingTheme>()
    val editingTheme: StateFlow<EditingTheme> get() = _editingTheme

    // 색상별 id가 없어 expanded 여부를 설정할 수 없으므로 List<Selectable<ColorDto>>로 따로 관리한다
    private val _editingColors = MutableStateFlow<List<Selectable<ColorDto>>>(emptyList())
    val editingColors: StateFlow<List<Selectable<ColorDto>>> get() = _editingColors

    val currentTable = currentTableRepository.currentTable

    private lateinit var originalTheme: TableTheme1

    fun initEditingTheme(isDarkMode: Boolean) {
        val themeId = savedStateHandle.get<String>("themeId")
        val theme = savedStateHandle.get<Int>("theme")

        if (theme == null || themeId == null) return

        if (theme != -1) { // 기본 제공 테마
            initBuiltInTheme(theme, isDarkMode)
        } else { // 커스텀 테마
            initCustomTheme(themeId, isDarkMode)
        }
    }

    private fun initBuiltInTheme(theme: Int, isDarkMode: Boolean) {
        try {
            originalTheme = BuiltInTheme1.fromCode(theme)
            _editingTheme.value = EditingTheme.fromTableTheme(originalTheme, isDarkMode)
        } catch (e: Exception) {
            apiOnError(e)
        }
    }

    private fun initCustomTheme(themeId: String, isDarkMode: Boolean) {
        originalTheme = if (themeId.isEmpty()) { // 새로 생성한 커스텀 테마
            CustomTheme1.Default
        } else { // 이미 존재하는 커스텀 테마
            try {
                themeRepository.getTheme(themeId)
            } catch (e: Exception) {
                apiOnError(e)
                CustomTheme1.Default
            }
        }
        _editingTheme.value = EditingTheme.fromTableTheme(originalTheme, isDarkMode)
    }

    fun addColor() {
        if (editingTheme.value.isEditable.not()) return

        val newColors = _editingTheme.value.colors.toMutableList().apply {
            add(ColorDto(fgColor = 0xffffff, bgColor = 0x1bd0c8).toDataWithState(true))
        }
        _editingTheme.value = editingTheme.value.copy(
            colors = newColors
        )
    }

    fun removeColor(index: Int) {
        if (editingTheme.value.isEditable.not()) return

        val newColors = editingTheme.value.colors.toMutableList().apply {
            removeAt(index)
        }
        _editingTheme.value = editingTheme.value.copy(
            colors = newColors
        )
    }

    fun updateColor(index: Int, fgColor: Int, bgColor: Int) {
        if (editingTheme.value.isEditable.not()) return

        val newColors = editingTheme.value.colors.toMutableList().apply {
            set(index, ColorDto(fgColor, bgColor).toDataWithState(get(index).state))
        }
        _editingTheme.value = editingTheme.value.copy(
            colors = newColors
        )
    }

    fun duplicateColor(index: Int) {
        if (editingTheme.value.isEditable.not()) return

        val newColors = editingTheme.value.colors.toMutableList().apply {
            add(index + 1, get(index).copy(state = false))
        }
        _editingTheme.value = editingTheme.value.copy(
            colors = newColors
        )
    }

    fun toggleColorExpanded(index: Int) {
        if (editingTheme.value.isEditable.not()) return

        val newColors = editingTheme.value.colors.toMutableList().apply {
            set(index, get(index).run { copy(state = !state) })
        }
        _editingTheme.value = editingTheme.value.copy(
            colors = newColors
        )
    }

    fun hasChange(): Boolean {
        return if (originalTheme.isEditable) {
            val originalCustomTheme = originalTheme as? CustomTheme1 ?: return false
            val editedCustomTheme = editingTheme.value.toCustomTheme(originalCustomTheme.id)

            return originalTheme.name != editedCustomTheme.name ||
                originalTheme.getColors(false) != editedCustomTheme.getColors(false)
        } else {
            false
        }
    }

    suspend fun saveTheme() {
        if (editingTheme.value.isEditable.not()) return
        val originalCustomTheme = originalTheme as? CustomTheme1 ?: return
        val editedCustomTheme = editingTheme.value.toCustomTheme(originalCustomTheme.id)

        originalTheme = if (originalCustomTheme.isNew) {
            themeRepository.createTheme(editedCustomTheme.name, editedCustomTheme.getColors(false))
        } else {
            themeRepository.updateTheme(editedCustomTheme.id, editedCustomTheme.name, editedCustomTheme.getColors(false))
        }
    }

    suspend fun applyThemeToCurrentTable() {
        val currentTable = currentTable.value ?: return
        val originalCustomTheme = originalTheme as? CustomTheme1 ?: return

        tableRepository.updateTableTheme(
            currentTable.id,
            originalCustomTheme.id
        )
    }

    suspend fun refreshCurrentTableIfNeeded() { // 현재 선택된 시간표의 테마라면 새로고침
        val currentTable = currentTable.value ?: return
        val originalCustomTheme = originalTheme as? CustomTheme1 ?: return

        if (currentTable.themeId == originalCustomTheme.id) {
            tableRepository.fetchTableById(currentTable.id)
        }
    }
}
