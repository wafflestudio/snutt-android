package com.wafflestudio.snutt2.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.lib.network.NetworkError
import com.wafflestudio.snutt2.lib.network.Result
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
            when (val result = testRepository.registerLocal(id, password, email)) {
                is Result.Success -> {
                    _testUiState.emit(TestUiState.Success(-1))
                }
                is Result.Fail -> {
                    _testUiState.emit(TestUiState.Fail)
                    handleTestError(result.error)
                }
            }
        }
    }

    fun runApiWithoutToken() {
        viewModelScope.launch {
            _testUiState.emit(TestUiState.Loading)
            testRepository.clearToken()

            when (val result = testRepository.getNotificationCount()) {
                is Result.Success -> {
                    _testUiState.emit(TestUiState.Success(result.data))
                }
                is Result.Fail -> {
                    _testUiState.emit(TestUiState.Fail)
                    handleTestError(result.error)
                }
            }
        }
    }

    fun getNotificationCount() {
        viewModelScope.launch {
            _testUiState.emit(TestUiState.Loading)
            when (val result = testRepository.getNotificationCount()) {
                is Result.Success -> {
                    _testUiState.emit(TestUiState.Success(result.data))
                }
                is Result.Fail -> {
                    _testUiState.emit(TestUiState.Fail)
                    handleTestError(result.error)
                }
            }
        }
    }

    private suspend fun handleTestError(error: NetworkError) {
        when (error) {
            // AuthError는 특수한 경우
            is NetworkError.AuthError -> {
                _testUiEvent.emit(TestUiEvent.ShowToast(error.displayMessage))
                testRepository.clearToken()
                _testUiEvent.emit(TestUiEvent.NavigateToOnboard)
            }
            // Local Exception
            is NetworkError.SignupError -> {
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
