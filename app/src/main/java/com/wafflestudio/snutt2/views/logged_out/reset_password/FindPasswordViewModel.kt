package com.wafflestudio.snutt2.views.logged_out.reset_password

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FindPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.CheckId(""))
    val uiState: StateFlow<UIState> = _uiState

    sealed class UIState {
        data class CheckId(
            val userId: String,
        ) : UIState()

        data object EnterFullEmail : UIState()

        data object VerifyCode : UIState()

        data object EnterNewPassword : UIState()
    }

    fun goToPreviousStep() {
        when (_uiState.value) {
            is UIState.CheckId -> {}
        }
    }

    suspend fun checkEmailById(userId: String) {}
}
