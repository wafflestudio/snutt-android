package com.wafflestudio.snutt2.ui.state

sealed interface SocialLoginState {
    data object Initial : SocialLoginState
    data object InProgress : SocialLoginState
    data object Cancelled : SocialLoginState
    data object Failed : SocialLoginState
    data class Success(
        val token: String,
    ) : SocialLoginState
}