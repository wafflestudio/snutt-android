package com.wafflestudio.snutt2.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.android.toast

class AuthCodeHandlerActivity : AppCompatActivity() {
    private val loginWithKakaoAccountCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                this.toast(getString(R.string.sign_in_kakao_failed_cancelled))
            } else if (error is AuthError && error.reason == AuthErrorCause.AccessDenied) {
                this.toast(getString(R.string.sign_in_kakao_failed_cancelled))
            }
        } else if (token == null) {
            this.toast(getString(R.string.sign_in_kakao_failed_unknown))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        this.toast(getString(R.string.sign_in_kakao_failed_cancelled))
                    } else if (error is AuthError && error.reason == AuthErrorCause.AccessDenied) {
                        this.toast(getString(R.string.sign_in_kakao_failed_cancelled))
                    } else {
                        // 카카오계정으로 로그인
                        UserApiClient.instance.loginWithKakaoAccount(context = this, callback = loginWithKakaoAccountCallback)
                    }
                } else if (token == null) {
                    this.toast(getString(R.string.sign_in_kakao_failed_unknown))
                }
            }
        } else {
            // 카카오계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(context = this, callback = loginWithKakaoAccountCallback)
        }
    }
}
