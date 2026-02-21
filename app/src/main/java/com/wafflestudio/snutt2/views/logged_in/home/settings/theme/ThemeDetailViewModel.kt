package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CustomTheme
import com.wafflestudio.snutt2.domainmodel.EditingTheme
import com.wafflestudio.snutt2.domainmodel.ThemeColor
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TableState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ThemeDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val themeRepository: ThemeRepository,
    private val tableRepository: TableRepository,
    currentTableRepository: CurrentTableRepository,
    userRepository: UserRepository,
    private val apiOnError: ApiOnError,
) : ViewModel() {

    private val editingTheme = MutableStateFlow<EditingTheme?>(null)
    private var isDarkMode = false

    val themeDetailUiState = combine(
        currentTableRepository.currentTable,
        userRepository.tableTrimParam,
        userRepository.tableLectureCustomOption,
        editingTheme,
    ) { table, trimParam, lectureCustomOption, editingTheme ->
        if (editingTheme == null) {
            return@combine ThemeDetailUiState.Loading
        }
        if (table == null) {
            return@combine ThemeDetailUiState.Error
        }

        val tableState = TableState(
            table,
            trimParam,
            lectureCustomOption,
            editingTheme.toTableTheme(),
        )
        ThemeDetailUiState.Success(
            editingTheme = editingTheme,
            tableState = tableState,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ThemeDetailUiState.Loading)

    val currentTable = currentTableRepository.currentTable

    fun initEditingTheme(isDarkMode: Boolean) {
        val themeId = savedStateHandle.toRoute<NavigationDestination.ThemeDetail>().themeId
        val theme = savedStateHandle.toRoute<NavigationDestination.ThemeDetail>().theme
        this.isDarkMode = isDarkMode

        if (theme != -1) { // 기본 제공 테마
            initBuiltInTheme(theme, isDarkMode)
        } else { // 커스텀 테마
            initCustomTheme(themeId, isDarkMode)
        }
    }

    private fun initBuiltInTheme(theme: Int, isDarkMode: Boolean) {
        try {
            val originalTheme = BuiltInTheme.fromCode(theme)
            editingTheme.value = EditingTheme.fromTableTheme(originalTheme, isDarkMode)
        } catch (e: Exception) {
            apiOnError(e)
        }
    }

    private fun initCustomTheme(themeId: String, isDarkMode: Boolean) {
        val originalTheme = if (themeId.isEmpty()) { // 새로 생성한 커스텀 테마
            CustomTheme.Default
        } else { // 이미 존재하는 커스텀 테마
            try {
                themeRepository.getTheme(themeId)
            } catch (e: Exception) {
                apiOnError(e)
                CustomTheme.Default
            }
        }
        editingTheme.value = EditingTheme.fromTableTheme(originalTheme, isDarkMode)
    }

    fun addColor() {
        val theme = editingTheme.value ?: return
        if (theme.isEditable.not()) return

        val newColors = theme.colors.toMutableList().apply {
            add(CustomTheme.Default.getColors(false).first().toDataWithState(true))
        }
        editingTheme.value = theme.copy(
            colors = newColors,
        )
    }

    fun removeColor(index: Int) {
        val theme = editingTheme.value ?: return
        if (theme.isEditable.not()) return

        val newColors = theme.colors.toMutableList().apply {
            removeAt(index)
        }
        editingTheme.value = theme.copy(colors = newColors)
    }

    fun updateColor(index: Int, fgColor: Int, bgColor: Int) {
        val theme = editingTheme.value ?: return
        if (theme.isEditable.not()) return

        val newColors = theme.colors.toMutableList().apply {
            set(index, ThemeColor(fgColor, bgColor).toDataWithState(get(index).state))
        }
        editingTheme.value = theme.copy(
            colors = newColors,
        )
    }

    fun duplicateColor(index: Int) {
        val theme = editingTheme.value ?: return
        if (theme.isEditable.not()) return

        val newColors = theme.colors.toMutableList().apply {
            add(index + 1, get(index).copy(state = false))
        }
        editingTheme.value = theme.copy(
            colors = newColors,
        )
    }

    fun toggleColorExpanded(index: Int) {
        val theme = editingTheme.value ?: return
        if (theme.isEditable.not()) return

        val newColors = theme.colors.toMutableList().apply {
            set(index, get(index).run { copy(state = !state) })
        }
        editingTheme.value = theme.copy(
            colors = newColors,
        )
    }

    suspend fun saveTheme() {
        val theme = editingTheme.value?.toTableTheme() as? CustomTheme ?: return
        if (theme.isEditable.not()) return

        val newTheme = if (theme.isNew) {
            themeRepository.createTheme(theme.name, theme.getColors(isDarkMode))
        } else {
            themeRepository.updateTheme(theme.id, theme.name, theme.getColors(isDarkMode))
        }

        editingTheme.value = EditingTheme.fromTableTheme(newTheme, isDarkMode)
    }

    suspend fun applyThemeToCurrentTable() {
        val currentTable = currentTable.value ?: return
        val theme = editingTheme.value?.toTableTheme() as? CustomTheme ?: return

        tableRepository.updateTableTheme(
            currentTable.id,
            theme.id,
        )
    }

    suspend fun refreshCurrentTableIfNeeded() { // 현재 선택된 시간표의 테마라면 새로고침
        val currentTable = currentTable.value ?: return
        val theme = editingTheme.value?.toTableTheme() as? CustomTheme ?: return

        if (theme.isAppliedToTable(currentTable)) {
            tableRepository.fetchTableById(currentTable.id)
        }
    }

    fun updateName(name: String) {
        val theme = editingTheme.value ?: return
        if (theme.isEditable.not()) return

        editingTheme.value = theme.copy(
            name = name,
        )
    }
}

sealed interface ThemeDetailUiState {
    class Success(
        val editingTheme: EditingTheme,
        val tableState: TableState,
    ) : ThemeDetailUiState

    data object Error : ThemeDetailUiState
    data object Loading : ThemeDetailUiState
}
