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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
                        ErrorCode.SERVER_FAULT -> Toast.makeText(
                            context,
                            context.getString(R.string.error_server_fault),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.INVALID_EMAIL -> Toast.makeText(
                            context,
                            context.getString(R.string.error_invalid_email),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.NO_FB_ID_OR_TOKEN -> Toast.makeText(
                            context,
                            context.getString(R.string.error_no_fb_id_or_token),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.NO_YEAR_OR_SEMESTER -> Toast.makeText(
                            context,
                            context.getString(R.string.error_no_year_or_semester),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.NOT_ENOUGH_TO_CREATE_TIMETABLE -> Toast.makeText(
                            context,
                            context.getString(R.string.error_not_enough_to_create_timetable),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.NO_LECTURE_INPUT -> Toast.makeText(
                            context,
                            context.getString(R.string.error_no_lecture_input),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.NO_LECTURE_ID -> Toast.makeText(
                            context,
                            context.getString(R.string.error_no_lecture_id),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.ATTEMPT_TO_MODIFY_IDENTITY -> Toast.makeText(
                            context,
                            context.getString(R.string.error_attempt_to_modify_identity),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.NO_TIMETABLE_TITLE -> Toast.makeText(
                            context,
                            context.getString(R.string.error_no_timetable_title),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.NO_REGISTRATION_ID -> Toast.makeText(
                            context,
                            context.getString(R.string.error_no_registration_id),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.INVALID_TIMEMASK -> Toast.makeText(
                            context,
                            context.getString(R.string.error_invalid_timemask),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.INVALID_COLOR -> Toast.makeText(
                            context,
                            context.getString(R.string.error_invalid_color),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.NO_LECTURE_TITLE -> Toast.makeText(
                            context,
                            context.getString(R.string.error_no_lecture_title),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.EXPIRED_PASSWORD_RESET_CODE -> Toast.makeText(
                            context,
                            context.getString(R.string.error_expired_verification_code),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.WRONG_PASSWORD_RESET_CODE -> Toast.makeText(
                            context,
                            context.getString(R.string.error_wrong_verification_code),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.WRONG_API_KEY -> Toast.makeText(
                            context,
                            context.getString(R.string.error_wrong_api_key),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.NO_USER_TOKEN -> Toast.makeText(
                            context,
                            context.getString(R.string.error_no_user_token),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.WRONG_USER_TOKEN -> {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_wrong_user_token),
                                Toast.LENGTH_SHORT,
                            ).show()
                            GlobalScope.launch {
                                try {
                                    userRepository.postForceLogout()
                                    userRepository.performLogout()
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "로그아웃에 실패하였습니다.",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }
                            }
                        }
                        ErrorCode.NO_ADMIN_PRIVILEGE -> Toast.makeText(
                            context,
                            context.getString(R.string.error_no_admin_privilege),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.WRONG_ID -> Toast.makeText(
                            context,
                            context.getString(R.string.error_wrong_id),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.WRONG_PASSWORD -> Toast.makeText(
                            context,
                            context.getString(R.string.error_wrong_password),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.WRONG_FB_TOKEN -> Toast.makeText(
                            context,
                            context.getString(R.string.error_wrong_fb_token),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.UNKNOWN_APP -> Toast.makeText(
                            context,
                            context.getString(R.string.error_unknown_app),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.INVALID_ID -> Toast.makeText(
                            context,
                            context.getString(R.string.error_invalid_id),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.INVALID_PASSWORD -> Toast.makeText(
                            context,
                            context.getString(R.string.error_invalid_password),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.DUPLICATE_ID -> Toast.makeText(
                            context,
                            context.getString(R.string.error_duplicate_id),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.DUPLICATE_TIMETABLE_TITLE -> Toast.makeText(
                            context,
                            context.getString(R.string.error_duplicate_timetable_title),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.DUPLICATE_LECTURE -> Toast.makeText(
                            context,
                            context.getString(R.string.error_duplicate_lecture),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.ALREADY_LOCAL_ACCOUNT -> Toast.makeText(
                            context,
                            context.getString(R.string.error_already_local_account),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.ALREADY_FB_ACCOUNT -> Toast.makeText(
                            context,
                            context.getString(R.string.error_already_fb_account),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.NOT_LOCAL_ACCOUNT -> Toast.makeText(
                            context,
                            context.getString(R.string.error_not_local_account),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.NOT_FB_ACCOUNT -> Toast.makeText(
                            context,
                            context.getString(R.string.error_not_fb_account),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.FB_ID_WITH_SOMEONE_ELSE -> Toast.makeText(
                            context,
                            context.getString(R.string.error_fb_id_with_someone_else),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.WRONG_SEMESTER -> Toast.makeText(
                            context,
                            context.getString(R.string.error_wrong_semester),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.NOT_CUSTOM_LECTURE -> Toast.makeText(
                            context,
                            context.getString(R.string.error_not_custom_lecture),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.LECTURE_TIME_OVERLAP -> Toast.makeText(
                            context,
                            context.getString(R.string.error_lecture_time_overlap),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.IS_CUSTOM_LECTURE -> Toast.makeText(
                            context,
                            context.getString(R.string.error_is_custom_lecture),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.EMAIL_NOT_VERIFIED -> Toast.makeText(
                            context,
                            context.getString(R.string.error_email_not_verified),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.TAG_NOT_FOUND -> Toast.makeText(
                            context,
                            context.getString(R.string.error_tag_not_found),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.TIMETABLE_NOT_FOUND -> Toast.makeText(
                            context,
                            context.getString(R.string.error_timetable_not_found),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.LECTURE_NOT_FOUND -> Toast.makeText(
                            context,
                            context.getString(R.string.error_lecture_not_found),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.REF_LECTURE_NOT_FOUND -> Toast.makeText(
                            context,
                            context.getString(R.string.error_ref_lecture_not_found),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.USER_NOT_FOUND -> Toast.makeText(
                            context,
                            context.getString(R.string.error_no_such_user_with_that_email),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.COLORLIST_NOT_FOUND -> Toast.makeText(
                            context,
                            context.getString(R.string.error_colorlist_not_found),
                            Toast.LENGTH_SHORT,
                        ).show()
                        ErrorCode.EMAIL_NOT_FOUND -> Toast.makeText(
                            context,
                            context.getString(R.string.error_no_registered_email),
                            Toast.LENGTH_SHORT,
                        ).show()
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
                            error.errorDTO?.message ?: context.getString(R.string.error_unknown),
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
    const val INVALID_EMAIL = 0x300F
    const val VACANCY_PREV_SEMESTER = 0x9C45
    const val VACANCY_DUPLICATE = 0x9FC4
    const val INVALID_NICKNAME = 0x9C48

    /* 401 - Request was invalid */
    const val NO_FB_ID_OR_TOKEN = 0x1001
    const val NO_YEAR_OR_SEMESTER = 0x1002
    const val NOT_ENOUGH_TO_CREATE_TIMETABLE = 0x1003
    const val NO_LECTURE_INPUT = 0x1004
    const val NO_LECTURE_ID = 0x1005
    const val ATTEMPT_TO_MODIFY_IDENTITY = 0x1006
    const val NO_TIMETABLE_TITLE = 0x1007
    const val NO_REGISTRATION_ID = 0x1008
    const val INVALID_TIMEMASK = 0x1009
    const val INVALID_COLOR = 0x100A
    const val NO_LECTURE_TITLE = 0x100B
    const val EXPIRED_PASSWORD_RESET_CODE = 0x2010
    const val WRONG_PASSWORD_RESET_CODE = 0x2011

    /* 403 - Authorization-related */
    const val WRONG_API_KEY = 0x2000
    const val NO_USER_TOKEN = 0x2001
    const val WRONG_USER_TOKEN = 0x2002
    const val NO_ADMIN_PRIVILEGE = 0x2003
    const val WRONG_ID = 0x2004
    const val WRONG_PASSWORD = 0x2005
    const val WRONG_FB_TOKEN = 0x2006
    const val UNKNOWN_APP = 0x2007

    /* 403 - Restrictions */
    const val INVALID_ID = 0x3000
    const val INVALID_PASSWORD = 0x3001
    const val DUPLICATE_ID = 0x3002
    const val DUPLICATE_TIMETABLE_TITLE = 0x3003
    const val DUPLICATE_LECTURE = 0x3004
    const val ALREADY_LOCAL_ACCOUNT = 0x3005
    const val ALREADY_FB_ACCOUNT = 0x3006
    const val NOT_LOCAL_ACCOUNT = 0x3007
    const val NOT_FB_ACCOUNT = 0x3008
    const val FB_ID_WITH_SOMEONE_ELSE = 0x3009
    const val WRONG_SEMESTER = 0x300A
    const val NOT_CUSTOM_LECTURE = 0x300B
    const val LECTURE_TIME_OVERLAP = 0x300C
    const val IS_CUSTOM_LECTURE = 0x300D
    const val EMAIL_NOT_VERIFIED = 0x3011

    /* 404 - NOT found */
    const val TAG_NOT_FOUND = 0x4000
    const val TIMETABLE_NOT_FOUND = 0x4001
    const val LECTURE_NOT_FOUND = 0x4002
    const val REF_LECTURE_NOT_FOUND = 0x4003
    const val USER_NOT_FOUND = 0x4004
    const val COLORLIST_NOT_FOUND = 0x4005
    const val EMAIL_NOT_FOUND = 0x4006
}

@JsonClass(generateAdapter = true)
data class ErrorDTO(
    @Json(name = "errcode") val code: Int? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "ext") val ext: Map<String, String>? = null,
)
