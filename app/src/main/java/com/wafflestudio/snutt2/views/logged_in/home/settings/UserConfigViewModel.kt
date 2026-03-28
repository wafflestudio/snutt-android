package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.isIdInvalid
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.isPasswordInvalid
import com.wafflestudio.snutt2.lib.network.AddLocalIdError
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.ChangePasswordError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
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
class UserConfigViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {
    private val _showChangePasswordDialog = MutableStateFlow(false)
    private val _showAddIdPasswordDialog = MutableStateFlow(false)
    private val _showLeaveDialog = MutableStateFlow(false)

    private val _userConfigUiEvent: MutableSharedFlow<UserConfigUiEvent> = MutableSharedFlow(replay = 1)
    val userConfigUiEvent = _userConfigUiEvent.asSharedFlow()

    fun addNewLocalId(id: String, password: String, passwordConfirm: String) {
        viewModelScope.launch {
            if (id.isIdInvalid()) {
                _userConfigUiEvent.emit(UserConfigUiEvent.ShowToastByEvent(UserConfigEvent.InvalidIdError))
                return@launch
            } else if (password.isPasswordInvalid()) {
                _userConfigUiEvent.emit(UserConfigUiEvent.ShowToastByEvent(UserConfigEvent.InvalidPasswordError))
                return@launch
            } else if (password != passwordConfirm) {
                _userConfigUiEvent.emit(UserConfigUiEvent.ShowToastByEvent(UserConfigEvent.PasswordMismatchError))
                return@launch
            } else {
                userRepository.postUserPassword(id, password)
                    .onSuccess {
                        _userConfigUiEvent.emit(UserConfigUiEvent.ShowToastByEvent(UserConfigEvent.AddIdPasswordSuccess))
                        fetchUserInfo()
                        hideAddIdPasswordDialog()
                    }
                    .onFailure { error ->
                        handleUserConfigError(error)
                    }
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String, newPasswordConfirm: String) {
        viewModelScope.launch {
            if (newPassword.isPasswordInvalid()) {
                _userConfigUiEvent.emit(UserConfigUiEvent.ShowToastByEvent(UserConfigEvent.InvalidPasswordError))
                return@launch
            } else if (newPassword != newPasswordConfirm) {
                _userConfigUiEvent.emit(UserConfigUiEvent.ShowToastByEvent(UserConfigEvent.PasswordMismatchError))
                return@launch
            } else {
                userRepository.putUserPassword(oldPassword, newPassword)
                    .onSuccess {
                        _userConfigUiEvent.emit(UserConfigUiEvent.ShowToastByEvent(UserConfigEvent.ChangePasswordSuccess))
                        hideChangePasswordDialog()
                    }
                    .onFailure { error ->
                        handleUserConfigError(error)
                    }
            }
        }
    }

    fun leave() {
        viewModelScope.launch {
            userRepository.deleteUserAccount()
                .onSuccess {
                    _showLeaveDialog.emit(false)
                    _userConfigUiEvent.emit(UserConfigUiEvent.NavigateToOnboard)
                }
                .onFailure { error ->
                    handleUserConfigError(error)
                }
        }
    }

    fun showChangePasswordDialog() {
        viewModelScope.launch {
            _showChangePasswordDialog.emit(true)
        }
    }

    fun hideChangePasswordDialog() {
        viewModelScope.launch {
            _showChangePasswordDialog.emit(false)
        }
    }

    fun showAddIdPasswordDialog() {
        viewModelScope.launch {
            _showAddIdPasswordDialog.emit(true)
        }
    }

    fun hideAddIdPasswordDialog() {
        viewModelScope.launch {
            _showAddIdPasswordDialog.emit(false)
        }
    }

    fun showLeaveDialog() {
        viewModelScope.launch {
            _showLeaveDialog.emit(true)
        }
    }

    fun hideLeaveDialog() {
        viewModelScope.launch {
            _showLeaveDialog.emit(false)
        }
    }

    fun resetToastMessage() {
        viewModelScope.launch {
            _userConfigUiEvent.emit(UserConfigUiEvent.ShowToast(""))
        }
    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            userRepository.fetchUserInfo()
                .onFailure { error ->
                    handleUserConfigError(error)
                }
        }
    }

    private suspend fun handleUserConfigError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _userConfigUiEvent.emit(UserConfigUiEvent.ShowToast(displayMessage))
                userRepository.performLogout()
                _userConfigUiEvent.emit(UserConfigUiEvent.NavigateToOnboard)
            }

            is AddLocalIdError -> {
                _userConfigUiEvent.emit(UserConfigUiEvent.ShowToast(displayMessage))
            }

            is ChangePasswordError -> {
                _userConfigUiEvent.emit(UserConfigUiEvent.ShowToast(displayMessage))
            }

            else -> {
                _userConfigUiEvent.emit(UserConfigUiEvent.ShowToast(displayMessage))
            }
        }
    }

    val userConfigUiState = combine(
        userRepository.user,
        _showChangePasswordDialog,
        _showAddIdPasswordDialog,
        _showLeaveDialog,
    ) { user, showChangePasswordDialog, showAddIdPasswordDialog, showLeaveDialog ->
        if (user == null) {
            UserConfigUiState.Default
        } else {
            UserConfigUiState(
                user.nickname?.getDisplayName() ?: "",
                user.localId,
                user.email,
                showChangePasswordDialog,
                showAddIdPasswordDialog,
                showLeaveDialog,
            )
        }
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserConfigUiState.Default)
}

data class UserConfigUiState(
    val userName: String,
    val localId: String?,
    val email: String?,
    val showChangePasswordDialog: Boolean,
    val showAddIdPasswordDialog: Boolean,
    val showLeaveDialog: Boolean,
) {
    companion object {
        val Default = UserConfigUiState("", "", "", false, false, false)
    }
}

sealed interface UserConfigUiEvent {
    data class ShowToast(val message: String) : UserConfigUiEvent
    data class ShowToastByEvent(val event: UserConfigEvent) : UserConfigUiEvent
    data object NavigateToOnboard : UserConfigUiEvent
}

enum class UserConfigEvent {
    InvalidIdError, InvalidPasswordError, PasswordMismatchError, ChangePasswordSuccess, AddIdPasswordSuccess
}
