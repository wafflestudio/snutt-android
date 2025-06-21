package com.wafflestudio.snutt2.lib.network

import com.wafflestudio.snutt2.lib.network.call_adapter.ErrorParsedHttpException
import kotlinx.coroutines.CancellationException
import okio.IOException

fun ErrorParsedHttpException.toDomainError(): DomainError {
    // 여기서는 Local Exception을 제외한 Global Exception을 translate 한다. Local Exception은 이미 translate 되어 여기로 오지 않는다.
    val displayMessage = this.errorDTO?.displayMessage ?: ""
    return when (this.errorDTO?.code) {
        ErrorCode.SERVER_FAULT -> ServerFault(displayMessage)
        ErrorCode.NO_ADMIN_PRIVILEGE -> NoAdminPrivilege(displayMessage)
        ErrorCode.UNKNOWN_APP -> UnknownApp(displayMessage)
        ErrorCode.WRONG_API_KEY -> AuthError.WrongApiKey(displayMessage)
        ErrorCode.NO_USER_TOKEN -> AuthError.NoUserToken(displayMessage)
        ErrorCode.WRONG_USER_TOKEN -> AuthError.WrongUserToken(displayMessage)
        else -> Unknown(displayMessage)
    }
}

fun Exception.toDomainError(): DomainError {
    // 여기서는 ErrorParsedHttpException를 제외한 Exception을 translate 한다. ErrorParsedHttpExceptionr은 이미 translate 되어 여기로 오지 않는다.
    return when (this) {
        is IOException -> NetworkDisconnect("")
        is CancellationException -> Nothing("")
        else -> Unknown("")
    }
}
