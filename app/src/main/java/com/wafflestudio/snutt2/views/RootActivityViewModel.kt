package com.wafflestudio.snutt2.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.lib.network.GlobalNetworkEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootActivityViewModel @Inject constructor() : ViewModel() {
    private val _globalNetworkUiState: MutableSharedFlow<GlobalNetworkUiState> = MutableSharedFlow()
    val globalNetworkUiState = _globalNetworkUiState

    fun onGlobalNetworkEvent(event: GlobalNetworkEvent) {
        viewModelScope.launch {
            when (event) {
                GlobalNetworkEvent.NETWORK_ERROR -> _globalNetworkUiState.emit(GlobalNetworkUiState.NetworkError)
                GlobalNetworkEvent.SERVER_FAULT -> _globalNetworkUiState.emit(GlobalNetworkUiState.ServerFault)
                GlobalNetworkEvent.WRONG_API_KEY -> _globalNetworkUiState.emit(GlobalNetworkUiState.WrongApiKey)
                GlobalNetworkEvent.NO_USER_TOKEN -> _globalNetworkUiState.emit(GlobalNetworkUiState.NoUserToken)
                GlobalNetworkEvent.WRONG_USER_TOKEN -> _globalNetworkUiState.emit(GlobalNetworkUiState.WrongUserToken)
                GlobalNetworkEvent.NO_ADMIN_PRIVILEGE -> _globalNetworkUiState.emit(GlobalNetworkUiState.NoAdminPrivilege)
                GlobalNetworkEvent.UNKNOWN_APP -> _globalNetworkUiState.emit(GlobalNetworkUiState.UnknownApp)
                GlobalNetworkEvent.UNKNOWN_ERROR -> _globalNetworkUiState.emit(GlobalNetworkUiState.UnknownError)
            }
        }
    }
}
