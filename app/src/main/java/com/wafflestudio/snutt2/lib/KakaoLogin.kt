package com.wafflestudio.snutt2.lib

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.android.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

suspend fun kakaoLogin(
    context: Context,
    coroutineScope: CoroutineScope,
    callbackOnSuccess: suspend (String) -> Unit,
) {
    val loginWithKakaoAccountCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                context.toast(context.getString(R.string.sign_in_kakao_failed_cancelled))
            } else if (error is AuthError && error.reason == AuthErrorCause.AccessDenied) {
                context.toast(context.getString(R.string.sign_in_kakao_failed_cancelled))
            } else {
                context.toast(context.getString(R.string.sign_in_kakao_failed_unknown))
            }
        } else if (token != null) {
            coroutineScope.launch {
                callbackOnSuccess(token.accessToken)
            }
        } else {
            context.toast(context.getString(R.string.sign_in_kakao_failed_unknown))
        }
    }

    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, loginError ->
            if (loginError != null) {
                if (loginError is ClientError && loginError.reason == ClientErrorCause.Cancelled) {
                    context.toast(context.getString(R.string.sign_in_kakao_failed_cancelled))
                } else if (loginError is AuthError && loginError.reason == AuthErrorCause.AccessDenied) {
                    context.toast(context.getString(R.string.sign_in_kakao_failed_cancelled))
                } else {
                    // 카카오계정으로 로그인
                    UserApiClient.instance.loginWithKakaoAccount(context = context, callback = loginWithKakaoAccountCallback)
                }
            } else if (token != null) {
                coroutineScope.launch {
                    callbackOnSuccess(token.accessToken)
                }
            } else {
                context.toast(context.getString(R.string.sign_in_kakao_failed_unknown))
            }
        }
    } else {
        // 카카오계정으로 로그인
        UserApiClient.instance.loginWithKakaoAccount(context = context, callback = loginWithKakaoAccountCallback)
    }
}
