package com.wafflestudio.snutt2.ui.state

enum class SocialLoginType {
    FACEBOOK,
    KAKAO,
    GOOGLE,
}

fun SocialLoginType.getString(): String {
    return when (this) {
        SocialLoginType.FACEBOOK -> "페이스북"
        SocialLoginType.GOOGLE -> "구글"
        SocialLoginType.KAKAO -> "카카오"
    }
}
