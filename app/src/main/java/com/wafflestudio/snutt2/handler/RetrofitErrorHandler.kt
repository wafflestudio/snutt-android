package com.wafflestudio.snutt2.handler

import android.content.Context
import android.content.Intent
import com.google.gson.annotations.SerializedName
import com.wafflestudio.snutt2.SNUTTBaseActivity
import com.wafflestudio.snutt2.ui.IntroActivity
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by makesource on 2017. 4. 28..
 */
// Refactoring FIXME: 일단 주석때림
class RetrofitErrorHandler(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        TODO("Not yet implemented")
    }
//    override fun handleError(cause: RetrofitError): Throwable {
//        val mHandler = Handler(Looper.getMainLooper())
//        if (cause.kind == RetrofitError.Kind.NETWORK) { // network error
//            mHandler.postDelayed(
//                {
//                    Toast.makeText(
//                        context,
//                        context.getString(R.string.error_no_network),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                },
//                0
//            )
//        } else {
//            val response = cause.response
//            if (response != null) {
//                try {
//                    val error = cause.getBodyAs(RestError::class.java) as RestError
//                    Log.d(TAG, error.code.toString() + " " + error.message)
//                    mHandler.postDelayed(
//                        {
//                            when (error.code) {
//                                ErrorCode.SERVER_FAULT -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_server_fault),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.NO_FB_ID_OR_TOKEN -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_no_fb_id_or_token),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.NO_YEAR_OR_SEMESTER -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_no_year_or_semester),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.NOT_ENOUGH_TO_CREATE_TIMETABLE -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_not_enough_to_create_timetable),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.NO_LECTURE_INPUT -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_no_lecture_input),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.NO_LECTURE_ID -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_no_lecture_id),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.ATTEMPT_TO_MODIFY_IDENTITY -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_attempt_to_modify_identity),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.NO_TIMETABLE_TITLE -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_no_timetable_title),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.NO_REGISTRATION_ID -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_no_registration_id),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.INVALID_TIMEMASK -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_invalid_timemask),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.INVALID_COLOR -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_invalid_color),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.NO_LECTURE_TITLE -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_no_lecture_title),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.WRONG_API_KEY -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_wrong_api_key),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.NO_USER_TOKEN -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_no_user_token),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.WRONG_USER_TOKEN -> {
//                                    Toast.makeText(
//                                        context,
//                                        context.getString(R.string.error_wrong_user_token),
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    val activity = lastActivity
//                                    val progressDialog = if (activity != null) ProgressDialog.show(
//                                        activity,
//                                        "로그아웃",
//                                        "잠시만 기다려 주세요",
//                                        true,
//                                        false
//                                    ) else null
//                                    // Refactoring FIXME: Unbounded
//                                    instance!!.postForceLogout()
//                                        .observeOn(AndroidSchedulers.mainThread())
//                                        .subscribeBy(onSuccess = {
//                                            instance!!.performLogout()
//                                            startIntro(context)
//                                            finishAll()
//                                            if (activity != null && progressDialog != null) {
//                                                progressDialog.dismiss()
//                                            }
//                                        }, onError = {
//                                            Toast.makeText(context, "로그아웃에 실패하였습니다.", Toast.LENGTH_SHORT).show()
//                                            progressDialog!!.dismiss()
//                                        })
//                                }
//                                ErrorCode.NO_ADMIN_PRIVILEGE -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_no_admin_privilege),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.WRONG_ID -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_wrong_id),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.WRONG_PASSWORD -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_wrong_password),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.WRONG_FB_TOKEN -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_wrong_fb_token),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.UNKNOWN_APP -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_unknown_app),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.INVALID_ID -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_invalid_id),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.INVALID_PASSWORD -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_invalid_password),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.DUPLICATE_ID -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_duplicate_id),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.DUPLICATE_TIMETABLE_TITLE -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_duplicate_timetable_title),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.DUPLICATE_LECTURE -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_duplicate_lecture),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.ALREADY_LOCAL_ACCOUNT -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_already_local_account),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.ALREADY_FB_ACCOUNT -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_already_fb_account),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.NOT_LOCAL_ACCOUNT -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_not_local_account),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.NOT_FB_ACCOUNT -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_not_fb_account),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.FB_ID_WITH_SOMEONE_ELSE -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_fb_id_with_someone_else),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.WRONG_SEMESTER -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_wrong_semester),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.NOT_CUSTOM_LECTURE -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_not_custom_lecture),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.LECTURE_TIME_OVERLAP -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_lecture_time_overlap),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.IS_CUSTOM_LECTURE -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_is_custom_lecture),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.TAG_NOT_FOUND -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_tag_not_found),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.TIMETABLE_NOT_FOUND -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_timetable_not_found),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.LECTURE_NOT_FOUND -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_lecture_not_found),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.REF_LECTURE_NOT_FOUND -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_ref_lecture_not_found),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ErrorCode.COLORLIST_NOT_FOUND -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_colorlist_not_found),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                else -> Toast.makeText(
//                                    context,
//                                    context.getString(R.string.error_unknown),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        },
//                        0
//                    )
//                } catch (e: Exception) {
//                    mHandler.postDelayed(
//                        {
//                            Toast.makeText(
//                                context,
//                                context.getString(R.string.error_unknown),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        },
//                        0
//                    )
//                }
//            }
//        }
//        return cause
//    }

    private fun startIntro(context: Context) {
        val intent = Intent(context, IntroActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private fun finishAll() {
        for (activity in SNUTTBaseActivity.activityList) {
            activity.finish()
        }
    }

    private val lastActivity: Context?
        private get() = try {
            val size = SNUTTBaseActivity.activityList.size
            SNUTTBaseActivity.activityList[size - 1]
        } catch (e: Exception) {
            null
        }

    companion object {
        private const val TAG = "RETROFIT_ERROR_HANDLER"
    }
}

internal enum class ErrorCode(private val value: Int) {
    SERVER_FAULT(0x0000), /* 401 - Request was invalid */
    NO_FB_ID_OR_TOKEN(0x1001), NO_YEAR_OR_SEMESTER(0x1002), NOT_ENOUGH_TO_CREATE_TIMETABLE(0x1003), NO_LECTURE_INPUT(
        0x1004
    ),
    NO_LECTURE_ID(0x1005), ATTEMPT_TO_MODIFY_IDENTITY(0x1006), NO_TIMETABLE_TITLE(0x1007), NO_REGISTRATION_ID(
        0x1008
    ),
    INVALID_TIMEMASK(0x1009), INVALID_COLOR(0x100A), NO_LECTURE_TITLE(0x100B), /* 403 - Authorization-related */
    WRONG_API_KEY(0x2000), NO_USER_TOKEN(0x2001), WRONG_USER_TOKEN(0x2002), NO_ADMIN_PRIVILEGE(
        0x2003
    ),
    WRONG_ID(0x2004), WRONG_PASSWORD(0x2005), WRONG_FB_TOKEN(0x2006), UNKNOWN_APP(0x2007), /* 403 - Restrictions */
    INVALID_ID(0x3000), INVALID_PASSWORD(0x3001), DUPLICATE_ID(0x3002), DUPLICATE_TIMETABLE_TITLE(
        0x3003
    ),
    DUPLICATE_LECTURE(0x3004), ALREADY_LOCAL_ACCOUNT(0x3005), ALREADY_FB_ACCOUNT(0x3006), NOT_LOCAL_ACCOUNT(
        0x3007
    ),
    NOT_FB_ACCOUNT(0x3008), FB_ID_WITH_SOMEONE_ELSE(0x3009), WRONG_SEMESTER(0x300A), NOT_CUSTOM_LECTURE(
        0x300B
    ),
    LECTURE_TIME_OVERLAP(0x300C), IS_CUSTOM_LECTURE(0x300D), /* 404 - NOT found */
    TAG_NOT_FOUND(0x4000), TIMETABLE_NOT_FOUND(0x4001), LECTURE_NOT_FOUND(0x4002), REF_LECTURE_NOT_FOUND(
        0x4003
    ),
    COLORLIST_NOT_FOUND(0x4005);
}

internal class RestError {
    @SerializedName("errcode")
    var code: ErrorCode? = null

    @SerializedName("message")
    var message: String? = null
}
