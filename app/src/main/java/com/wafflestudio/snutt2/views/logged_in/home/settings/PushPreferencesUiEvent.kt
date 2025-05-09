package com.wafflestudio.snutt2.views.logged_in.home.settings

sealed interface PushPreferencesUiEvent {
    data class ShowToast(val message: String) : PushPreferencesUiEvent
}
