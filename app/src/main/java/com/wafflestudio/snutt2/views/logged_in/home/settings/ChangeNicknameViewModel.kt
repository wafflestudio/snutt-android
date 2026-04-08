package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeNicknameViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<ChangeNicknameUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val initialNickname: String = userRepository.user.value?.nickname?.nickname ?: ""

    fun changeNickname(nickname: String) {
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

sealed interface ChangeNicknameUiEvent {
    data class ShowToast(val message: String) : ChangeNicknameUiEvent
    data object Success : ChangeNicknameUiEvent
}
