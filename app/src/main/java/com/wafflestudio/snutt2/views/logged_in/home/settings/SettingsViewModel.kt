package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.logging.AnalyticsEvent
import com.wafflestudio.snutt2.lib.logging.AnalyticsLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val analyticsLogger: AnalyticsLogger,
    private val remoteConfig: RemoteConfig,
) : ViewModel() {

    private val showLogoutDialog = MutableStateFlow(false)

    private val _logoutFinishedUiEvent: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 1)
    val logoutFinishedUiEvent = _logoutFinishedUiEvent.asSharedFlow()

    fun showLogoutDialog() {
        viewModelScope.launch {
            showLogoutDialog.emit(true)
        }
    }

    fun hideLogoutDialog() {
        viewModelScope.launch {
            showLogoutDialog.emit(false)
        }
    }

    fun performLogout() {
        viewModelScope.launch {
            runCatching {
                analyticsLogger.logEvent(AnalyticsEvent.Logout)
                userRepository.postForceLogout()
                userRepository.performLogout()
                showLogoutDialog.emit(false)
                _logoutFinishedUiEvent.emit(Unit)
            }.onFailure {
                showLogoutDialog.emit(false)
            }
        }
    }

    val settingsUiState = combine(
        userRepository.user,
        userRepository.themeMode,
        showLogoutDialog,
        remoteConfig.settingPageNewBadgeTitles,
    ) { user, themeMode, showLogoutDialog, settingPageNewBadgeTitles ->
        if (user == null) {
            return@combine SettingsUiState.Error
        }

        SettingsUiState.Success(
            user.nickname?.nickname ?: "",
            themeMode.toString(),
            showLogoutDialog,
            settingPageNewBadgeTitles,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsUiState.Loading)

}


sealed interface SettingsUiState {
    data class Success(
        val userName: String,
        val themeModeName: String,
        val showLogoutDialog: Boolean,
        val settingPageNewBadgeTitles: List<String>,
    ) : SettingsUiState

    data object Loading : SettingsUiState
    data object Error : SettingsUiState
}
