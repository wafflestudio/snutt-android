package com.wafflestudio.snutt_staging.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.wafflestudio.snutt_staging.SNUTTApplication;

import java.io.IOException;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

import static com.wafflestudio.snutt_staging.handler.ErrorCode.*;

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
        if (cause.getKind() == RetrofitError.Kind.NETWORK) { // network error

        } else {
            final Response response = cause.getResponse();
            if (response != null) {
                final RestError error = (RestError) cause.getBodyAs(RestError.class);
                Log.d(TAG, error.code + " " + error.message);
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (error.code) {
                            case WRONG_ID:
                                Toast.makeText(context, "잘못된 ID입니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case WRONG_PASSWORD:
                                Toast.makeText(context, "잘못된 비밀번호입니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case WRONG_FB_TOKEN:
                                Toast.makeText(context, "잘못된 페이스북 토큰입니다.", Toast.LENGTH_SHORT).show();
                                break;
                        }

                    }
                }, 0);
            }
        }
        return cause;
    }

}

enum ErrorCode {
    @SerializedName("0") SERVER_DEFAULT(0x0000),
    /* 401 - Request was invalid */
    @SerializedName("4097") NO_FB_ID_OR_TOKEN(0x1001),
    @SerializedName("4098") NO_YEAR_OR_SEMESTER(0x1002),
    @SerializedName("4099") NOT_ENOUGH_TO_CREATE_TIMETABLE(0x1003),
    @SerializedName("4100") NO_LECTURE_INPUT(0x1004),
    @SerializedName("4101") NO_LECTURE_ID(0x1005),
    @SerializedName("4102") ATTEMPT_TO_MODIFY_IDENTITY(0x1006),
    @SerializedName("4103") NO_TIMETABLE_ID(0x1007),
    @SerializedName("4104") NO_REGISTRATION_ID(0x1008),
    @SerializedName("4105") INVALID_TIMEMASK(0x1009),
    @SerializedName("4106") INVALID_COLOR (0x100A),
    /* 403 - Authorization-related */
    @SerializedName("8192") WRONG_API_KEY(0x2000),
    @SerializedName("8193") NO_USER_TOKEN(0x2001),
    @SerializedName("8194") WRONG_USER_TOKEN(0x2002),
    @SerializedName("8195") NO_ADMIN_PRIVILEGE(0x2003),
    @SerializedName("8196") WRONG_ID(8196),
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
    @SerializedName("16389") COLORLIST_NOT_FOUND(0x4003);
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