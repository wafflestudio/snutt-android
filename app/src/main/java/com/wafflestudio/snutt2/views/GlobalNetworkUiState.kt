package com.wafflestudio.snutt2.views

sealed interface GlobalNetworkUiState {
    data object NetworkError : GlobalNetworkUiState
    data object ServerFault : GlobalNetworkUiState
    data object WrongApiKey : GlobalNetworkUiState
    data object NoUserToken : GlobalNetworkUiState
    data object WrongUserToken : GlobalNetworkUiState
    data object NoAdminPrivilege : GlobalNetworkUiState
    data object UnknownApp : GlobalNetworkUiState
    data object UnknownError : GlobalNetworkUiState
}
