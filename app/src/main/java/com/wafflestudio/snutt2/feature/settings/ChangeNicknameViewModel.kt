package com.wafflestudio.snutt2.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeNicknameViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    val initialNickname: String = userRepository.user.value?.nickname?.nickname ?: ""

    private val _uiState = MutableStateFlow(ChangeNicknameUiState(nicknameField = initialNickname))
    val uiState: StateFlow<ChangeNicknameUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ChangeNicknameUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onNicknameFieldChange(value: String) {
        _uiState.update { it.copy(nicknameField = value) }
    }

    fun changeNickname() {
        val nickname = _uiState.value.nicknameField
        if (nickname.isEmpty() || nickname == initialNickname) return
        viewModelScope.launch {
            userRepository.patchUserInfo(nickname)
                .onSuccess {
                    _uiEvent.emit(ChangeNicknameUiEvent.Success)
                }
                .onFailure { handleError(it) }
        }
    }

    private suspend fun handleError(error: DomainError) {
        _uiEvent.emit(ChangeNicknameUiEvent.ShowToast(displayMessageResolver.getDisplayMessage(error)))
    }
}

data class ChangeNicknameUiState(
    val nicknameField: String = "",
)

sealed interface ChangeNicknameUiEvent {
    data class ShowToast(val message: String) : ChangeNicknameUiEvent
    data object Success : ChangeNicknameUiEvent
}
