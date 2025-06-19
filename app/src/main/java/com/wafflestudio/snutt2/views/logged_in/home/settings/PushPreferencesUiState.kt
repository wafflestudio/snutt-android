package com.wafflestudio.snutt2.views.logged_in.home.settings

import com.wafflestudio.snutt2.domainmodel.PushPreferences

sealed interface PushPreferencesUiState {
    data object Loading : PushPreferencesUiState
    data class Success(val pushPreferences: PushPreferences) : PushPreferencesUiState
    data object Error : PushPreferencesUiState
}
