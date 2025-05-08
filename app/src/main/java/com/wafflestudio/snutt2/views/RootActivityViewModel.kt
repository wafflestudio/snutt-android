package com.wafflestudio.snutt2.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.lib.network.GlobalNetworkEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootActivityViewModel @Inject constructor() : ViewModel() {
    private val _globalNetworkUiEvent: MutableSharedFlow<GlobalNetworkUiEvent> = MutableSharedFlow()
    val globalNetworkUiEvent = _globalNetworkUiEvent.asSharedFlow()

    fun onGlobalNetworkEvent(event: GlobalNetworkEvent) {
        viewModelScope.launch {
            when (event) {
                GlobalNetworkEvent.ERROR_NETWORK -> _globalNetworkUiEvent.emit(GlobalNetworkUiEvent.NetworkError)
                GlobalNetworkEvent.ERROR_SERVER_FAULT -> _globalNetworkUiEvent.emit(GlobalNetworkUiEvent.ServerFault)
                GlobalNetworkEvent.ERROR_WRONG_API_KEY -> _globalNetworkUiEvent.emit(GlobalNetworkUiEvent.WrongApiKey)
                GlobalNetworkEvent.ERROR_NO_USER_TOKEN -> _globalNetworkUiEvent.emit(GlobalNetworkUiEvent.NoUserToken)
                GlobalNetworkEvent.ERROR_WRONG_USER_TOKEN -> _globalNetworkUiEvent.emit(GlobalNetworkUiEvent.WrongUserToken)
                GlobalNetworkEvent.ERROR_NO_ADMIN_PRIVILEGE -> _globalNetworkUiEvent.emit(GlobalNetworkUiEvent.NoAdminPrivilege)
                GlobalNetworkEvent.ERROR_UNKNOWN_APP -> _globalNetworkUiEvent.emit(GlobalNetworkUiEvent.UnknownApp)
                GlobalNetworkEvent.ERROR_UNKNOWN -> _globalNetworkUiEvent.emit(GlobalNetworkUiEvent.UnknownError)
            }
        }
    }
}
