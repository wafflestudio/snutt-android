package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.isIdInvalid
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.isPasswordInvalid
import com.wafflestudio.snutt2.domain.AuthError
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserConfigViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserConfigUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UserConfigUiEvent> = MutableSharedFlow(replay = 1)
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            userRepository.user.collect { user ->
                _uiState.update {
                    it.copy(
                        userName = user?.nickname?.getDisplayName() ?: "",
                        localId = user?.localId,
                        email = user?.email,
                    )
                }
            }
        }
    }

    fun addNewLocalId(id: String, password: String, passwordConfirm: String) {
        viewModelScope.launch {
            if (id.isIdInvalid()) {
                _uiEvent.emit(UserConfigUiEvent.ShowToastByEvent(UserConfigEvent.InvalidIdError))
                return@launch
            } else if (password.isPasswordInvalid()) {
                _uiEvent.emit(UserConfigUiEvent.ShowToastByEvent(UserConfigEvent.InvalidPasswordError))
                return@launch
            } else if (password != passwordConfirm) {
                _uiEvent.emit(UserConfigUiEvent.ShowToastByEvent(UserConfigEvent.PasswordMismatchError))
                return@launch
            } else {
                userRepository.postUserPassword(id, password)
                    .onSuccess {
                        _uiEvent.emit(UserConfigUiEvent.ShowToastByEvent(UserConfigEvent.AddIdPasswordSuccess))
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
                _uiEvent.emit(UserConfigUiEvent.ShowToastByEvent(UserConfigEvent.InvalidPasswordError))
                return@launch
            } else if (newPassword != newPasswordConfirm) {
                _uiEvent.emit(UserConfigUiEvent.ShowToastByEvent(UserConfigEvent.PasswordMismatchError))
                return@launch
            } else {
                userRepository.putUserPassword(oldPassword, newPassword)
                    .onSuccess {
                        _uiEvent.emit(UserConfigUiEvent.ShowToastByEvent(UserConfigEvent.ChangePasswordSuccess))
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
                    hideLeaveDialog()
                    _uiEvent.emit(UserConfigUiEvent.NavigateToOnboard)
                }
                .onFailure { error ->
                    handleUserConfigError(error)
                }
        }
    }

    fun showChangePasswordDialog() {
        _uiState.update { it.copy(dialogState = UserConfigUiState.DialogState.ChangePassword) }
    }

    fun hideChangePasswordDialog() {
        _uiState.update { it.copy(dialogState = UserConfigUiState.DialogState.None) }
    }

    fun showAddIdPasswordDialog() {
        _uiState.update { it.copy(dialogState = UserConfigUiState.DialogState.AddIdPassword) }
    }

    fun hideAddIdPasswordDialog() {
        _uiState.update { it.copy(dialogState = UserConfigUiState.DialogState.None) }
    }

    fun showLeaveDialog() {
        _uiState.update { it.copy(dialogState = UserConfigUiState.DialogState.Leave) }
    }

    fun hideLeaveDialog() {
        _uiState.update { it.copy(dialogState = UserConfigUiState.DialogState.None) }
    }

    fun resetToastMessage() {
        viewModelScope.launch {
            _uiEvent.emit(UserConfigUiEvent.ShowToast(""))
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
                _uiEvent.emit(UserConfigUiEvent.ShowToast(displayMessage))
                userRepository.performLogout()
                _uiEvent.emit(UserConfigUiEvent.NavigateToOnboard)
            }

            else -> {
                _uiEvent.emit(UserConfigUiEvent.ShowToast(displayMessage))
            }
        }
    }
}

data class UserConfigUiState(
    val userName: String = "",
    val localId: String? = "",
    val email: String? = "",
    val dialogState: DialogState = DialogState.None,
) {
    sealed interface DialogState {
        data object None : DialogState
        data object ChangePassword : DialogState
        data object AddIdPassword : DialogState
        data object Leave : DialogState
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
