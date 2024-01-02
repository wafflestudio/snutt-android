package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.lib.network.dto.core.ThemeDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeConfigViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
) : ViewModel() {

    private val _themes = MutableStateFlow<List<ThemeDto>>(emptyList())
    val themes: StateFlow<List<ThemeDto>> get() = _themes

    init {
        viewModelScope.launch {
            fetchCustomThemes()
        }
    }

    suspend fun fetchCustomThemes() {
        _themes.value = themeRepository.getThemes()
    }

    suspend fun deleteCustomTheme(theme: ThemeDto) {
        themeRepository.deleteTheme(theme)
        fetchCustomThemes()
    }

    suspend fun duplicateCustomTheme(theme: ThemeDto) {
        themeRepository.createTheme(theme.copy(isDefault = false))
        fetchCustomThemes()
    }
}
