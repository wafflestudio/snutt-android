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

    suspend fun deleteTheme(themeId: String) {
        themeRepository.deleteTheme(themeId)
        fetchThemes()
    }

    suspend fun copyTheme(themeId: String) {
        themeRepository.copyTheme(themeId)
        fetchThemes()
    }
}
