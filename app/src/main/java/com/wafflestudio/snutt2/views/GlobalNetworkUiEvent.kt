package com.wafflestudio.snutt2.views

sealed interface GlobalNetworkUiEvent {
    data object NetworkError : GlobalNetworkUiEvent
    data object ServerFault : GlobalNetworkUiEvent
    data object WrongApiKey : GlobalNetworkUiEvent
    data object NoUserToken : GlobalNetworkUiEvent
    data object WrongUserToken : GlobalNetworkUiEvent
    data object NoAdminPrivilege : GlobalNetworkUiEvent
    data object UnknownApp : GlobalNetworkUiEvent
    data object UnknownError : GlobalNetworkUiEvent
}
