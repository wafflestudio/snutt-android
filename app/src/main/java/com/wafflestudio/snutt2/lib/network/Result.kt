package com.wafflestudio.snutt2.lib.network

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Fail(val error: NetworkError) : Result<Nothing>()
}

sealed interface NetworkError {
    val displayMessage: String

    // 1. Global Errors (모든 API에서 발생 가능)
    // Auth 관련 오류
    sealed interface AuthError : NetworkError {
        data class WrongApiKey(override val displayMessage: String) : AuthError
        data class NoUserToken(override val displayMessage: String) : AuthError // 사실 발생하지 않는 오류일 수 있음. empty token을 보내도 WrongUserToken 발생.
        data class WrongUserToken(override val displayMessage: String) : AuthError
    }

    // 기타 오류
    data class NetworkDisconnect(override val displayMessage: String) : NetworkError
    data class ServerFault(override val displayMessage: String) : NetworkError
    data class NoAdminPrivilege(override val displayMessage: String) : NetworkError
    data class UnknownApp(override val displayMessage: String) : NetworkError
    data class Unknown(override val displayMessage: String) : NetworkError
    data class Nothing(override val displayMessage: String) : NetworkError

    // 2. Local Errors (특정 API에서만 발생)
    // 회원가입 API
    sealed interface SignupError : NetworkError {
        data class InvalidId(override val displayMessage: String) : SignupError
        data class InvalidPassword(override val displayMessage: String) : SignupError
        data class DuplicateId(override val displayMessage: String) : SignupError
        data class UsedEmail(override val displayMessage: String) : SignupError
    }
}
