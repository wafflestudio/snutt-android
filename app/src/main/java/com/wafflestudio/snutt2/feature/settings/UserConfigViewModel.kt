package com.wafflestudio.snutt2.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.AuthError
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.lib.isIdInvalid
import com.wafflestudio.snutt2.lib.isPasswordInvalid
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

    fun onAddIdPasswordIdChange(value: String) {
        _uiState.update {
            val dialog = it.dialogState as? UserConfigUiState.DialogState.AddIdPassword ?: return@update it
            it.copy(dialogState = dialog.copy(id = value))
        }
    }

    fun onAddIdPasswordPasswordChange(value: String) {
        _uiState.update {
            val dialog = it.dialogState as? UserConfigUiState.DialogState.AddIdPassword ?: return@update it
            it.copy(dialogState = dialog.copy(password = value))
        }
    }

    fun onAddIdPasswordPasswordConfirmChange(value: String) {
        _uiState.update {
            val dialog = it.dialogState as? UserConfigUiState.DialogState.AddIdPassword ?: return@update it
            it.copy(dialogState = dialog.copy(passwordConfirm = value))
        }
    }

    fun addNewLocalId() {
        val dialog = _uiState.value.dialogState as? UserConfigUiState.DialogState.AddIdPassword ?: return
        val id = dialog.id
        val password = dialog.password
        val passwordConfirm = dialog.passwordConfirm
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

    fun onChangePasswordCurrentChange(value: String) {
        _uiState.update {
            val dialog = it.dialogState as? UserConfigUiState.DialogState.ChangePassword ?: return@update it
            it.copy(dialogState = dialog.copy(currentPassword = value))
        }
    }

    fun onChangePasswordNewChange(value: String) {
        _uiState.update {
            val dialog = it.dialogState as? UserConfigUiState.DialogState.ChangePassword ?: return@update it
            it.copy(dialogState = dialog.copy(newPassword = value))
        }
    }

    fun onChangePasswordNewConfirmChange(value: String) {
        _uiState.update {
            val dialog = it.dialogState as? UserConfigUiState.DialogState.ChangePassword ?: return@update it
            it.copy(dialogState = dialog.copy(newPasswordConfirm = value))
        }
    }

    fun changePassword() {
        val dialog = _uiState.value.dialogState as? UserConfigUiState.DialogState.ChangePassword ?: return
        val oldPassword = dialog.currentPassword
        val newPassword = dialog.newPassword
        val newPasswordConfirm = dialog.newPasswordConfirm
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
        _uiState.update { it.copy(dialogState = UserConfigUiState.DialogState.ChangePassword()) }
    }

    fun hideChangePasswordDialog() {
        _uiState.update { it.copy(dialogState = UserConfigUiState.DialogState.None) }
    }

    fun showAddIdPasswordDialog() {
        _uiState.update { it.copy(dialogState = UserConfigUiState.DialogState.AddIdPassword()) }
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
        data class ChangePassword(
            val currentPassword: String = "",
            val newPassword: String = "",
            val newPasswordConfirm: String = "",
        ) : DialogState
        data class AddIdPassword(
            val id: String = "",
            val password: String = "",
            val passwordConfirm: String = "",
        ) : DialogState
        data object Leave : DialogState
    }
}

sealed interface UserConfigUiEvent {
    data class ShowToast(val message: String) : UserConfigUiEvent
    data class ShowToastByEvent(val event: UserConfigEvent) : UserConfigUiEvent
    data object NavigateToOnboard : UserConfigUiEvent
}

enum class UserConfigEvent {
    InvalidIdError,
    InvalidPasswordError,
    PasswordMismatchError,
    ChangePasswordSuccess,
    AddIdPasswordSuccess,
}
