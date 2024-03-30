package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeListViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val tableRepository: TableRepository,
    currentTableRepository: CurrentTableRepository,
) : ViewModel() {

    val customThemes: StateFlow<List<CustomTheme>> get() = themeRepository.customThemes
    val builtInThemes: StateFlow<List<BuiltInTheme>> get() = themeRepository.builtInThemes

    val currentTable = currentTableRepository.currentTable

    suspend fun fetchThemes() {
        themeRepository.fetchThemes()
    }

    suspend fun deleteThemeAndRefreshTableIfNeeded(themeId: String) { // 현재 선택된 시간표의 테마라면 서버에서 변경된 색 배치를 불러옴
        themeRepository.deleteTheme(themeId)
        currentTable.value?.let {
            if (it.themeId != null && it.themeId == themeId) {
                tableRepository.fetchTableById(it.id)
            }
        }
    }

    suspend fun copyTheme(themeId: String) {
        themeRepository.copyTheme(themeId)
    }
}
