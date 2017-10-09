package com.wafflestudio.snutt_staging.handler;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.manager.UserManager;
import com.wafflestudio.snutt_staging.ui.IntroActivity;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.wafflestudio.snutt_staging.SNUTTBaseActivity.activityList;

/**
 * Created by makesource on 2017. 4. 28..
 */

public class RetrofitErrorHandler implements ErrorHandler {
    private final static String TAG = "RETROFIT_ERROR_HANDLER";
    private Context context;

    public RetrofitErrorHandler(Context context) {
        this.context = context;
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        if (cause.getKind() == RetrofitError.Kind.NETWORK) { // network error
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, context.getString(R.string.error_no_network), Toast.LENGTH_SHORT).show();
                }
            }, 0);
        } else {
            final Response response = cause.getResponse();
            if (response != null) {
                try {
                    final RestError error = (RestError) cause.getBodyAs(RestError.class);
                    Log.d(TAG, error.code + " " + error.message);

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switch (error.code) {
                                case SERVER_FAULT:
                                    Toast.makeText(context, context.getString(R.string.error_server_fault), Toast.LENGTH_SHORT).show();
                                    break;
                                case NO_FB_ID_OR_TOKEN:
                                    Toast.makeText(context, context.getString(R.string.error_no_fb_id_or_token), Toast.LENGTH_SHORT).show();
                                    break;
                                case NO_YEAR_OR_SEMESTER:
                                    Toast.makeText(context, context.getString(R.string.error_no_year_or_semester), Toast.LENGTH_SHORT).show();
                                    break;
                                case NOT_ENOUGH_TO_CREATE_TIMETABLE:
                                    Toast.makeText(context, context.getString(R.string.error_not_enough_to_create_timetable), Toast.LENGTH_SHORT).show();
                                    break;
                                case NO_LECTURE_INPUT:
                                    Toast.makeText(context, context.getString(R.string.error_no_lecture_input), Toast.LENGTH_SHORT).show();
                                    break;
                                case NO_LECTURE_ID:
                                    Toast.makeText(context, context.getString(R.string.error_no_lecture_id), Toast.LENGTH_SHORT).show();
                                    break;
                                case ATTEMPT_TO_MODIFY_IDENTITY:
                                    Toast.makeText(context, context.getString(R.string.error_attempt_to_modify_identity), Toast.LENGTH_SHORT).show();
                                    break;
                                case NO_TIMETABLE_TITLE:
                                    Toast.makeText(context, context.getString(R.string.error_no_timetable_title), Toast.LENGTH_SHORT).show();
                                    break;
                                case NO_REGISTRATION_ID:
                                    Toast.makeText(context, context.getString(R.string.error_no_registration_id), Toast.LENGTH_SHORT).show();
                                    break;
                                case INVALID_TIMEMASK:
                                    Toast.makeText(context, context.getString(R.string.error_invalid_timemask), Toast.LENGTH_SHORT).show();
                                    break;
                                case INVALID_COLOR:
                                    Toast.makeText(context, context.getString(R.string.error_invalid_color), Toast.LENGTH_SHORT).show();
                                    break;
                                case NO_LECTURE_TITLE:
                                    Toast.makeText(context, context.getString(R.string.error_no_lecture_title), Toast.LENGTH_SHORT).show();
                                    break;
                                case WRONG_API_KEY:
                                    Toast.makeText(context, context.getString(R.string.error_wrong_api_key), Toast.LENGTH_SHORT).show();
                                    break;
                                case NO_USER_TOKEN:
                                    Toast.makeText(context, context.getString(R.string.error_no_user_token), Toast.LENGTH_SHORT).show();
                                    break;
                                case WRONG_USER_TOKEN:
                                    Toast.makeText(context, context.getString(R.string.error_wrong_user_token), Toast.LENGTH_SHORT).show();
                                    UserManager.getInstance().performLogout();
                                    startIntro(context);
                                    finishAll();
                                    break;
                                case NO_ADMIN_PRIVILEGE:
                                    Toast.makeText(context, context.getString(R.string.error_no_admin_privilege), Toast.LENGTH_SHORT).show();
                                    break;
                                case WRONG_ID:
                                    Toast.makeText(context, context.getString(R.string.error_wrong_id), Toast.LENGTH_SHORT).show();
                                    break;
                                case WRONG_PASSWORD:
                                    Toast.makeText(context, context.getString(R.string.error_wrong_password), Toast.LENGTH_SHORT).show();
                                    break;
                                case WRONG_FB_TOKEN:
                                    Toast.makeText(context, context.getString(R.string.error_wrong_fb_token), Toast.LENGTH_SHORT).show();
                                    break;
                                case UNKNOWN_APP:
                                    Toast.makeText(context, context.getString(R.string.error_unknown_app), Toast.LENGTH_SHORT).show();
                                    break;
                                case INVALID_ID:
                                    Toast.makeText(context, context.getString(R.string.error_invalid_id), Toast.LENGTH_SHORT).show();
                                    break;
                                case INVALID_PASSWORD:
                                    Toast.makeText(context, context.getString(R.string.error_invalid_password), Toast.LENGTH_SHORT).show();
                                    break;
                                case DUPLICATE_ID:
                                    Toast.makeText(context, context.getString(R.string.error_duplicate_id), Toast.LENGTH_SHORT).show();
                                    break;
                                case DUPLICATE_TIMETABLE_TITLE:
                                    Toast.makeText(context, context.getString(R.string.error_duplicate_timetable_title), Toast.LENGTH_SHORT).show();
                                    break;
                                case DUPLICATE_LECTURE:
                                    Toast.makeText(context, context.getString(R.string.error_duplicate_lecture), Toast.LENGTH_SHORT).show();
                                    break;
                                case ALREADY_LOCAL_ACCOUNT:
                                    Toast.makeText(context, context.getString(R.string.error_already_local_account), Toast.LENGTH_SHORT).show();
                                    break;
                                case ALREADY_FB_ACCOUNT:
                                    Toast.makeText(context, context.getString(R.string.error_already_fb_account), Toast.LENGTH_SHORT).show();
                                    break;
                                case NOT_LOCAL_ACCOUNT:
                                    Toast.makeText(context, context.getString(R.string.error_not_local_account), Toast.LENGTH_SHORT).show();
                                    break;
                                case NOT_FB_ACCOUNT:
                                    Toast.makeText(context, context.getString(R.string.error_not_fb_account), Toast.LENGTH_SHORT).show();
                                    break;
                                case FB_ID_WITH_SOMEONE_ELSE:
                                    Toast.makeText(context, context.getString(R.string.error_fb_id_with_someone_else), Toast.LENGTH_SHORT).show();
                                    break;
                                case WRONG_SEMESTER:
                                    Toast.makeText(context, context.getString(R.string.error_wrong_semester), Toast.LENGTH_SHORT).show();
                                    break;
                                case NOT_CUSTOM_LECTURE:
                                    Toast.makeText(context, context.getString(R.string.error_not_custom_lecture), Toast.LENGTH_SHORT).show();
                                    break;
                                case LECTURE_TIME_OVERLAP:
                                    Toast.makeText(context, context.getString(R.string.error_lecture_time_overlap), Toast.LENGTH_SHORT).show();
                                    break;
                                case IS_CUSTOM_LECTURE:
                                    Toast.makeText(context, context.getString(R.string.error_is_custom_lecture), Toast.LENGTH_SHORT).show();
                                    break;
                                case TAG_NOT_FOUND:
                                    Toast.makeText(context, context.getString(R.string.error_tag_not_found), Toast.LENGTH_SHORT).show();
                                    break;
                                case TIMETABLE_NOT_FOUND:
                                    Toast.makeText(context, context.getString(R.string.error_timetable_not_found), Toast.LENGTH_SHORT).show();
                                    break;
                                case LECTURE_NOT_FOUND:
                                    Toast.makeText(context, context.getString(R.string.error_lecture_not_found), Toast.LENGTH_SHORT).show();
                                    break;
                                case REF_LECTURE_NOT_FOUND:
                                    Toast.makeText(context, context.getString(R.string.error_ref_lecture_not_found), Toast.LENGTH_SHORT).show();
                                    break;
                                case COLORLIST_NOT_FOUND:
                                    Toast.makeText(context, context.getString(R.string.error_colorlist_not_found), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(context, context.getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                                    break;
                            }

                        }
                    }, 0);
                } catch (Exception e) {
                    Toast.makeText(context, context.getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                }
            }
        }
        return cause;
    }

    private void startIntro(Context context) {
        Intent intent = new Intent(context, IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void finishAll() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }

}

enum ErrorCode {
    @SerializedName("0") SERVER_FAULT(0x0000),
    /* 401 - Request was invalid */
    @SerializedName("4097") NO_FB_ID_OR_TOKEN(0x1001),
    @SerializedName("4098") NO_YEAR_OR_SEMESTER(0x1002),
    @SerializedName("4099") NOT_ENOUGH_TO_CREATE_TIMETABLE(0x1003),
    @SerializedName("4100") NO_LECTURE_INPUT(0x1004),
    @SerializedName("4101") NO_LECTURE_ID(0x1005),
    @SerializedName("4102") ATTEMPT_TO_MODIFY_IDENTITY(0x1006),
    @SerializedName("4103") NO_TIMETABLE_TITLE(0x1007),
    @SerializedName("4104") NO_REGISTRATION_ID(0x1008),
    @SerializedName("4105") INVALID_TIMEMASK(0x1009),
    @SerializedName("4106") INVALID_COLOR (0x100A),
    @SerializedName("4107") NO_LECTURE_TITLE (0x100B),
    /* 403 - Authorization-related */
    @SerializedName("8192") WRONG_API_KEY(0x2000),
    @SerializedName("8193") NO_USER_TOKEN(0x2001),
    @SerializedName("8194") WRONG_USER_TOKEN(0x2002),
    @SerializedName("8195") NO_ADMIN_PRIVILEGE(0x2003),
    @SerializedName("8196") WRONG_ID(0x2004),
    @SerializedName("8197") WRONG_PASSWORD(0x2005),
    @SerializedName("8198") WRONG_FB_TOKEN(0x2006),
    @SerializedName("8199") UNKNOWN_APP(0x2007),
    /* 403 - Restrictions */
    @SerializedName("12288") INVALID_ID(0x3000),
    @SerializedName("12289") INVALID_PASSWORD(0x3001),
    @SerializedName("12290") DUPLICATE_ID(0x3002),
    @SerializedName("12291") DUPLICATE_TIMETABLE_TITLE(0x3003),
    @SerializedName("12292") DUPLICATE_LECTURE(0x3004),
    @SerializedName("12293") ALREADY_LOCAL_ACCOUNT(0x3005),
    @SerializedName("12294") ALREADY_FB_ACCOUNT(0x3006),
    @SerializedName("12295") NOT_LOCAL_ACCOUNT(0x3007),
    @SerializedName("12296") NOT_FB_ACCOUNT(0x3008),
    @SerializedName("12297") FB_ID_WITH_SOMEONE_ELSE(0x3009),
    @SerializedName("12298") WRONG_SEMESTER(0x300A),
    @SerializedName("12299") NOT_CUSTOM_LECTURE(0x300B),
    @SerializedName("12300") LECTURE_TIME_OVERLAP(0x300C),
    @SerializedName("12301") IS_CUSTOM_LECTURE(0x300D),
    /* 404 - NOT found */
    @SerializedName("16384") TAG_NOT_FOUND(0x4000),
    @SerializedName("16385") TIMETABLE_NOT_FOUND(0x4001),
    @SerializedName("16386") LECTURE_NOT_FOUND(0x4002),
    @SerializedName("16387") REF_LECTURE_NOT_FOUND(0x4003),
    @SerializedName("16389") COLORLIST_NOT_FOUND(0x4005);
    private int value;
    ErrorCode(int value) {
        this.value = value;
    }
}

class RestError {
    @SerializedName("errcode")
    public ErrorCode code;
    @SerializedName("message")
    public String message;
}