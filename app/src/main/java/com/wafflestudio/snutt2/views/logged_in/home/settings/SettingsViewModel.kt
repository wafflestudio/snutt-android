package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.ui.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    userRepository: UserRepository,
) : ViewModel() {

    val settingsUiState = combine(
        userRepository.user,
        userRepository.themeMode,
    ) { user, themeMode ->
        if (user == null) {
            return@combine SettingsUiState.Error
        }

        SettingsUiState.Success(
            user.nickname?.nickname ?: "",
            themeMode,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsUiState.Loading)

}


sealed interface SettingsUiState {
    data class Success(
        val userName: String,
        val themeMode: ThemeMode,
    ) : SettingsUiState

    data object Loading : SettingsUiState
    data object Error : SettingsUiState
}
