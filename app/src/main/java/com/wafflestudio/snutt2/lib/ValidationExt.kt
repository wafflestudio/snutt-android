package com.wafflestudio.snutt2.lib

fun String.isEmailInvalid(): Boolean {
    val regex = Regex(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
            "@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+",
    )
    return this.isEmpty() || regex.matches(this).not()
}

fun String.isPasswordInvalid(): Boolean =
    Regex("^(?=.*\\d)(?=.*[a-zA-Z])\\S{6,20}\$").matches(this).not()

fun String.isIdInvalid(): Boolean = Regex("^[A-Za-z\\d]{4,32}\$").matches(this).not()
