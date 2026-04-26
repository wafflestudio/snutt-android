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
class AppReportViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AppReportUiState(email = userRepository.user.value?.email ?: ""),
    )
    val uiState: StateFlow<AppReportUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AppReportUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onDetailChange(value: String) {
        _uiState.update { it.copy(detail = value) }
    }

    fun sendFeedback() {
        val state = _uiState.value
        _uiState.update { it.copy(sentEnabled = false) }
        viewModelScope.launch {
            userRepository.postFeedback(state.email, state.detail)
                .onSuccess {
                    _uiEvent.emit(AppReportUiEvent.Success)
                }
                .onFailure {
                    _uiState.update { current -> current.copy(sentEnabled = true) }
                    handleError(it)
                }
        }
    }

    private suspend fun handleError(error: DomainError) {
        _uiEvent.emit(AppReportUiEvent.ShowToast(displayMessageResolver.getDisplayMessage(error)))
    }
}

data class AppReportUiState(
    val email: String = "",
    val detail: String = "",
    val sentEnabled: Boolean = true,
)

sealed interface AppReportUiEvent {
    data class ShowToast(val message: String) : AppReportUiEvent
    data object Success : AppReportUiEvent
}
