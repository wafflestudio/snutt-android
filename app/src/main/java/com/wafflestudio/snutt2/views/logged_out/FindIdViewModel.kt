package com.wafflestudio.snutt2.views.logged_out

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
class FindIdViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<FindIdUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun findIdByEmail(email: String) {
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

sealed interface FindIdUiEvent {
    data class ShowToast(val message: String) : FindIdUiEvent
    data class Success(val email: String) : FindIdUiEvent
}
