package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ColorModeSelectViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ColorModeSelectUiState())
    val uiState: StateFlow<ColorModeSelectUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.themeMode.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userRepository.setThemeMode(mode)
        }
    }
}

data class ColorModeSelectUiState(
    val themeMode: ThemeMode = ThemeMode.AUTO,
)