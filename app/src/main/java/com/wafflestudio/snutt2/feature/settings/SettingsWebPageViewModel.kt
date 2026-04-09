package com.wafflestudio.snutt2.feature.settings

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
class SettingsWebPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsWebPageUiState())
    val uiState: StateFlow<SettingsWebPageUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.themeMode.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
    }
}

data class SettingsWebPageUiState(
    val themeMode: ThemeMode = ThemeMode.AUTO,
)
