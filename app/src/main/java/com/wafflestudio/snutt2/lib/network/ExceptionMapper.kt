package com.wafflestudio.snutt2.lib.network

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.wafflestudio.snutt2.lib.network.error.ErrorParsedHttpException
import kotlinx.coroutines.CancellationException
import okio.IOException
import timber.log.Timber
import java.io.EOFException

fun Exception.toDomainError(): DomainError {
    Timber.e(this)

    return when (this) {
        is EOFException -> EOF("", "")
        is IOException -> NetworkDisconnect("", "")
        is CancellationException -> Nothing("", "")
        is ErrorParsedHttpException -> {
            val displayTitle = this.displayTitle ?: ""
            val displayMessage = this.displayMessage ?: ""
            return when (this.code) {
                ErrorCode.SERVER_FAULT -> ServerFault(displayTitle, displayMessage)
                ErrorCode.NO_ADMIN_PRIVILEGE -> NoAdminPrivilege(displayTitle, displayMessage)
                ErrorCode.WRONG_API_KEY -> WrongApiKey(displayTitle, displayMessage)
                ErrorCode.NO_USER_TOKEN -> NoUserToken(displayTitle, displayMessage)
                ErrorCode.WRONG_USER_TOKEN -> WrongUserToken(displayTitle, displayMessage)

                ErrorCode.INVALID_ID -> InvalidId(displayTitle, displayMessage)
                ErrorCode.INVALID_PASSWORD -> InvalidPassword(displayTitle, displayMessage)
                ErrorCode.DUPLICATE_ID -> DuplicateId(displayTitle, displayMessage)
                ErrorCode.USED_EMAIL -> UsedEmail(displayTitle, displayMessage)
                ErrorCode.WRONG_PASSWORD -> WrongPassword(displayTitle, displayMessage)
                ErrorCode.PAST_SEMESTER -> PastSemester(displayTitle, displayMessage)
                ErrorCode.DISABLED_FEATURE -> DisabledFeature(displayTitle, displayMessage)
                ErrorCode.LECTURE_TIME_OVERLAP -> LectureOverlap(displayTitle, displayMessage)
                else -> Unknown(displayTitle, displayMessage)
            }
        }

        else -> {
            FirebaseCrashlytics.getInstance().recordException(this)
            Unknown("", "")
        }
    }
}
