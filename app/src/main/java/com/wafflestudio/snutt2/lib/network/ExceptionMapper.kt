package com.wafflestudio.snutt2.lib.network

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.wafflestudio.snutt2.lib.network.call_adapter.ErrorParsedHttpException
import kotlinx.coroutines.CancellationException
import okio.IOException
import timber.log.Timber
import java.io.EOFException

fun Exception.toDomainError(): DomainError {
    Timber.e(this)

    return when (this) {
        is EOFException -> EOF("")
        is IOException -> NetworkDisconnect("")
        is CancellationException -> Nothing("")
        is ErrorParsedHttpException -> {
            val displayMessage = this.errorDTO?.displayMessage ?: ""
            return when (this.errorDTO?.code) {
                ErrorCode.SERVER_FAULT -> ServerFault(displayMessage)
                ErrorCode.NO_ADMIN_PRIVILEGE -> NoAdminPrivilege(displayMessage)
                ErrorCode.WRONG_API_KEY -> WrongApiKey(displayMessage)
                ErrorCode.NO_USER_TOKEN -> NoUserToken(displayMessage)
                ErrorCode.WRONG_USER_TOKEN -> WrongUserToken(displayMessage)

                ErrorCode.INVALID_ID -> InvalidId(displayMessage)
                ErrorCode.INVALID_PASSWORD -> InvalidPassword(displayMessage)
                ErrorCode.DUPLICATE_ID -> DuplicateId(displayMessage)
                ErrorCode.USED_EMAIL -> UsedEmail(displayMessage)
                ErrorCode.WRONG_PASSWORD -> WrongPassword(displayMessage)
                ErrorCode.PAST_SEMESTER -> PastSemester(displayMessage)
                else -> Unknown(displayMessage)
            }
        }
        else -> {
            FirebaseCrashlytics.getInstance().recordException(this)
            Unknown("")
        }
    }
}
