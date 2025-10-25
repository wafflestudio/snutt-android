package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CustomTheme
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.lib.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeConfigViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val tableRepository: TableRepository,
    currentTableRepository: CurrentTableRepository,
) : ViewModel() {

    val customThemes = themeRepository.customThemes
    val myCustomThemes = themeRepository.customThemes.map(viewModelScope) { customThemes ->
        customThemes.filter { it.isFromMarket.not() }
    }
    val marketCustomThemes = themeRepository.customThemes.map(viewModelScope) { customThemes ->
        customThemes.filter { it.isFromMarket }
    }
    val builtInThemes: StateFlow<List<BuiltInTheme>> = themeRepository.builtInThemes

    val currentTable = currentTableRepository.currentTable

    suspend fun fetchThemes() {
        themeRepository.fetchThemes()
    }

    suspend fun deleteThemeAndRefreshTableIfNeeded(theme: TableTheme) { // 현재 선택된 시간표의 테마라면 서버에서 변경된 색 배치를 불러옴
        if (theme !is CustomTheme) return
        themeRepository.deleteTheme(theme.id)

        val currentTable = currentTable.value ?: return
        if (theme.isAppliedToTable(currentTable)) {
            tableRepository.fetchTableById(currentTable.id)
        }
    }

    suspend fun copyTheme(theme: TableTheme) {
        if (theme !is CustomTheme) return
        themeRepository.copyTheme(theme.id)
    }

    suspend fun applyThemeToCurrentTable(theme: TableTheme) {
        val currentTable = currentTable.value ?: return
        when (theme) {
            is CustomTheme -> {
                tableRepository.updateTableTheme(
                    currentTable.id,
                    theme.id,
                )
            }

            is BuiltInTheme -> {
                tableRepository.updateTableTheme(
                    currentTable.id,
                    theme.code,
                )
            }
        }
    }
}
