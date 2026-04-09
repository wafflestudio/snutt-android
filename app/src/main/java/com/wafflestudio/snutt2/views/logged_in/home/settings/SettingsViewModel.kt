package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.logging.AnalyticsEvent
import com.wafflestudio.snutt2.logging.AnalyticsLogger
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import com.wafflestudio.snutt2.ui.theme.ThemeMode
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
            analyticsLogger.logEvent(AnalyticsEvent.Logout)
            userRepository.postForceLogout()
            userRepository.performLogout()
                .onFailure {
                    showLogoutDialog.emit(false)
                }.onSuccess {
                    showLogoutDialog.emit(false)
                    _logoutFinishedUiEvent.emit(Unit)
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
            return@combine SettingsUiState.DEFAULT
        }

        SettingsUiState(
            user.nickname?.getDisplayName() ?: "",
            themeMode,
            showLogoutDialog,
            settingPageNewBadgeTitles,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsUiState.DEFAULT)
}

data class SettingsUiState(
    val userName: String,
    val themeMode: ThemeMode,
    val showLogoutDialog: Boolean,
    val settingPageNewBadgeTitles: List<String>,
) {
    companion object {
        val DEFAULT = SettingsUiState("", ThemeMode.AUTO, false, emptyList())
    }
}
