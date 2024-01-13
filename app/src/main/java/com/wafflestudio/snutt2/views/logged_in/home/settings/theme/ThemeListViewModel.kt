package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeListViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
) : ViewModel() {

    val customThemes: StateFlow<List<CustomTheme>> get() = themeRepository.customThemes
    val builtInThemes: StateFlow<List<BuiltInTheme>> get() = themeRepository.builtInThemes

    suspend fun fetchThemes() {
        themeRepository.fetchThemes()
    }

    suspend fun setThemeDefault(themeId: String) {
        themeRepository.setCustomThemeDefault(themeId)
    }

    suspend fun setThemeDefault(code: Int) {
        themeRepository.setBuiltInThemeDefault(code)
    }

    suspend fun unsetThemeDefault(themeId: String) {
        themeRepository.unsetCustomThemeDefault(themeId)
    }

    suspend fun unsetThemeDefault(code: Int) {
        themeRepository.setBuiltInThemeDefault(0) // FIXME: DELETE /themes/basic/~/default 나오면 바꾸기
    }

    suspend fun deleteTheme(themeId: String) {
        themeRepository.deleteTheme(themeId)
    }

    suspend fun copyTheme(themeId: String) {
        themeRepository.copyTheme(themeId)
    }
}
