package com.wafflestudio.snutt2.lib.network

import com.wafflestudio.snutt2.lib.network.call_adapter.ErrorParsedHttpException
import kotlinx.coroutines.CancellationException
import okio.IOException

fun Exception.toDomainError(): DomainError {
    return when (this) {
        is IOException -> NetworkDisconnect("")
        is CancellationException -> Nothing("")
        is ErrorParsedHttpException -> {
            val displayMessage = this.errorDTO?.displayMessage ?: ""
            return when (this.errorDTO?.code) {
                ErrorCode.SERVER_FAULT -> ServerFault(displayMessage)
                ErrorCode.NO_ADMIN_PRIVILEGE -> NoAdminPrivilege(displayMessage)
                ErrorCode.WRONG_API_KEY -> AuthError.WrongApiKey(displayMessage)
                ErrorCode.NO_USER_TOKEN -> AuthError.NoUserToken(displayMessage)
                ErrorCode.WRONG_USER_TOKEN -> AuthError.WrongUserToken(displayMessage)

                ErrorCode.INVALID_ID -> SignupError.InvalidId(displayMessage)
                ErrorCode.INVALID_PASSWORD -> SignupError.InvalidPassword(displayMessage)
                ErrorCode.DUPLICATE_ID -> SignupError.DuplicateId(displayMessage)
                ErrorCode.USED_EMAIL -> SignupError.UsedEmail(displayMessage)
                else -> Unknown(displayMessage)
            }
        }
        else -> Unknown("")
    }
}
