package com.wafflestudio.snutt2.ui.state

sealed class SocialLoginProgress(open val type: SocialLoginType?) {
    data class Initial(override val type: SocialLoginType?) : SocialLoginProgress(null)
    data class InProgress(override val type: SocialLoginType?) : SocialLoginProgress(null)
    data class Cancelled(override val type: SocialLoginType) : SocialLoginProgress(type)
    data class Failed(override val type: SocialLoginType) : SocialLoginProgress(type)
    data class Success(override val type: SocialLoginType?) : SocialLoginProgress(null)
}
