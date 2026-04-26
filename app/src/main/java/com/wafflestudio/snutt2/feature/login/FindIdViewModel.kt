package com.wafflestudio.snutt2.feature.login

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
class FindIdViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FindIdUiState())
    val uiState: StateFlow<FindIdUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<FindIdUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEmailFieldChange(value: String) {
        _uiState.update { it.copy(emailField = value) }
    }

    fun findIdByEmail() {
        val email = _uiState.value.emailField
        viewModelScope.launch {
            userRepository.findIdByEmail(email)
                .onSuccess {
                    _uiEvent.emit(FindIdUiEvent.Success(email))
                }
                .onFailure { handleError(it) }
        }
    }

    private suspend fun handleError(error: DomainError) {
        _uiEvent.emit(FindIdUiEvent.ShowToast(displayMessageResolver.getDisplayMessage(error)))
    }
}

data class FindIdUiState(
    val emailField: String = "",
)

sealed interface FindIdUiEvent {
    data class ShowToast(val message: String) : FindIdUiEvent
    data class Success(val email: String) : FindIdUiEvent
}
