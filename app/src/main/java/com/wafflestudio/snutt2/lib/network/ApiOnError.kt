package com.wafflestudio.snutt2.lib.network

import android.content.Context
import android.widget.Toast
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.android.MessagingError
import com.wafflestudio.snutt2.lib.android.runOnUiThread
import com.wafflestudio.snutt2.lib.network.call_adapter.ErrorParsedHttpException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by makesource on 2017. 4. 28..
 */
@Singleton
class ApiOnError @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi,
    private val userRepository: UserRepository,
) : (Throwable) -> Unit {

    override fun invoke(error: Throwable) {
        runOnUiThread {
            Timber.e(error)

            when (error) {
                is IOException -> { // network error
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_no_network),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                is MessagingError -> {
                    Toast.makeText(
                        context,
                        error.message,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                is ErrorParsedHttpException -> {
                    when (error.errorDTO?.code) {
                        ErrorCode.WRONG_USER_TOKEN -> {
                            Toast.makeText(
                                context,
                                error.errorDTO.displayMessage,
                                Toast.LENGTH_SHORT,
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    userRepository.performLogout()
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.error_logout_fail),
                                            Toast.LENGTH_SHORT,
                                        )
                                            .show()
                                    }
                                }
                            }
                        }
                        ErrorCode.VACANCY_PREV_SEMESTER -> Toast.makeText(
                            context,
                            context.getString(R.string.error_vacancy_previous_semester),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.VACANCY_DUPLICATE -> Toast.makeText(
                            context,
                            context.getString(R.string.error_vacancy_duplicate),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.INVALID_NICKNAME -> Toast.makeText(
                            context,
                            context.getString(R.string.error_invalid_nickname),
                            Toast.LENGTH_SHORT,
                        ).show()
                        else -> Toast.makeText(
                            context,
                            error.errorDTO?.displayMessage ?: context.getString(R.string.error_unknown),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
                is kotlinx.coroutines.CancellationException -> {} // do nothing
                else -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_unknown),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }
}

object ErrorCode {
    const val SERVER_FAULT = 0x0000

    /* 400 - Bad request */
    const val VACANCY_PREV_SEMESTER = 0x9C45
    const val VACANCY_DUPLICATE = 0x9FC4
    const val USED_EMAIL = 0x9FC5
    const val INVALID_NICKNAME = 0x9C48
    const val ALREADY_LOCAL_ACCOUNT = 0x9C53
    const val ALREADY_SOCIAL_ACCOUNT = 0x9C54

    /* 401 - Request was invalid */
    const val NO_TIMETABLE_TITLE = 0x1007
    const val INVALID_TIME = 0x100C

    /* 403 - Authorization-related */
    const val WRONG_API_KEY = 0x2000
    const val NO_USER_TOKEN = 0x2001
    const val WRONG_USER_TOKEN = 0x2002
    const val NO_ADMIN_PRIVILEGE = 0x2003
    const val WRONG_ID = 0x2004
    const val WRONG_PASSWORD = 0x2005

    /* 403 - Restrictions */
    const val INVALID_ID = 0x3000
    const val INVALID_PASSWORD = 0x3001
    const val DUPLICATE_ID = 0x3002
    const val DUPLICATE_TIMETABLE_TITLE = 0x3003
    const val DUPLICATE_LECTURE = 0x3004
    const val WRONG_SEMESTER = 0x300A
    const val LECTURE_TIME_OVERLAP = 0x300C
    const val IS_CUSTOM_LECTURE = 0x300D
    const val INVALID_EMAIL = 0x300F
    const val EMAIL_NOT_VERIFIED = 0x3011

    /* 404 - NOT found */
    const val REF_LECTURE_NOT_FOUND = 0x4003
    const val USER_NOT_FOUND = 0x4004
}

@JsonClass(generateAdapter = true)
data class ErrorDTO(
    @Json(name = "errcode") val code: Int? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "displayMessage") val displayMessage: String? = null,
    @Json(name = "ext") val ext: Map<String, String>? = null,
)
