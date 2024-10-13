package com.wafflestudio.snutt2.ui.state

sealed class SocialLoginState(open val type: SocialLoginType?) {
    data class Initial(override val type: SocialLoginType?) : SocialLoginState(null)
    data class InProgress(override val type: SocialLoginType) : SocialLoginState(type)
    data class Cancelled(override val type: SocialLoginType) : SocialLoginState(type)
    data class Failed(override val type: SocialLoginType) : SocialLoginState(type)
    data class Success(
        override val type: SocialLoginType,
        val token: String,
    ) : SocialLoginState(type)
}


fun SocialLoginState.isProcessing(): Boolean = this is SocialLoginState.InProgress
