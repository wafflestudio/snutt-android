package com.wafflestudio.snutt2.model

enum class SocialLoginType {
    FACEBOOK,
    KAKAO,
    GOOGLE,
    NONE,
}

fun SocialLoginType.getString(): String {
    return when (this) {
        SocialLoginType.NONE -> ""
        SocialLoginType.FACEBOOK -> "페이스북"
        SocialLoginType.GOOGLE -> "구글"
        SocialLoginType.KAKAO -> "카카오"
    }
}

fun SocialLoginType.showDialog() = (this != SocialLoginType.NONE)
