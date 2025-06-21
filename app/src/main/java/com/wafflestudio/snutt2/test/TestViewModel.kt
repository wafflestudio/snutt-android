package com.wafflestudio.snutt2.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.SignupError
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val testRepository: TestRepository,
) : ViewModel() {
    private val _testUiState = MutableStateFlow<TestUiState>(TestUiState.Initial)
    val testUiState = _testUiState.asStateFlow()

    private val _testUiEvent: MutableSharedFlow<TestUiEvent> = MutableSharedFlow(replay = 1)
    val testUiEvent = _testUiEvent.asSharedFlow()

    fun registerLocal(id: String, password: String, email: String) {
        viewModelScope.launch {
            _testUiState.emit(TestUiState.Loading)
            testRepository.registerLocal(id, password, email)
                .onSuccess {
                    _testUiState.emit(TestUiState.Success(-1))
                }
                .onFailure { error ->
                    _testUiState.emit(TestUiState.Fail)
                    handleTestError(error)
                }
        }
    }

    fun runApiWithoutToken() {
        viewModelScope.launch {
            _testUiState.emit(TestUiState.Loading)
            testRepository.clearToken()

            testRepository.getNotificationCount()
                .onSuccess { data ->
                    _testUiState.emit(TestUiState.Success(data))
                }
                .onFailure { error ->
                    _testUiState.emit(TestUiState.Fail)
                    handleTestError(error)
                }
        }
    }

    fun getNotificationCount() {
        viewModelScope.launch {
            _testUiState.emit(TestUiState.Loading)

            testRepository.getNotificationCount()
                .onSuccess { data ->
                    _testUiState.emit(TestUiState.Success(data))
                }
                .onFailure { error ->
                    _testUiState.emit(TestUiState.Fail)
                    handleTestError(error)
                }
        }
    }

    private suspend fun handleTestError(error: DomainError) {
        when (error) {
            // Local Exception 중 특수한 경우가 있다면 여기에서 처리 (여기에서는 없음, 순서도 상관없음)
            // AuthError는 Global Exception 중 특수한 경우
            is AuthError -> {
                _testUiEvent.emit(TestUiEvent.ShowToast(error.displayMessage))
                testRepository.clearToken()
                _testUiEvent.emit(TestUiEvent.NavigateToOnboard)
            }
            // 특수하지 않은 Local Exception
            is SignupError -> {
                _testUiEvent.emit(TestUiEvent.ShowToast(error.displayMessage))
            }
            // 특수하지 않은 Global Exception
            else -> {
                _testUiEvent.emit(TestUiEvent.ShowToast(error.displayMessage))
            }
        }
    }
}

sealed interface TestUiState {
    data object Initial : TestUiState
    data object Loading : TestUiState
    data object Fail : TestUiState
    data class Success(val data: Int) : TestUiState
}

sealed interface TestUiEvent {
    data class ShowToast(val message: String) : TestUiEvent
    data object NavigateToOnboard : TestUiEvent
}
