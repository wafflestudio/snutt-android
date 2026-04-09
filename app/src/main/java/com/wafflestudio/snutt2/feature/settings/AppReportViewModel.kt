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
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppReportViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<AppReportUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val initialEmail: String = userRepository.user.value?.email ?: ""

    fun sendFeedback(email: String, detail: String) {
        viewModelScope.launch {
            userRepository.postFeedback(email, detail)
                .onSuccess {
                    _uiEvent.emit(AppReportUiEvent.Success)
                }
                .onFailure { handleError(it) }
        }
    }

    private suspend fun handleError(error: DomainError) {
        _uiEvent.emit(AppReportUiEvent.ShowToast(displayMessageResolver.getDisplayMessage(error)))
    }
}

sealed interface AppReportUiEvent {
    data class ShowToast(val message: String) : AppReportUiEvent
    data object Success : AppReportUiEvent
}
