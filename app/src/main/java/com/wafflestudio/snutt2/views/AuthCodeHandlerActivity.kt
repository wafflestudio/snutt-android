package com.wafflestudio.snutt2.views

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

class AuthCodeHandlerActivity : AppCompatActivity() {
    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            // Log.d("aaaa", "로그인 실패 $error") TODO:
            Log.d("plgafhdflow","7")
        } else if (token != null) {
            // Log.d("aaaa", "로그인 성공 ${token.accessToken}") TODO:
            Log.d("plgafhdflow","8")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            // 카카오톡 로그인
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                // 로그인 실패 부분
                if (error != null) {
                    Log.d("plgafhdflow","9")
                    // Log.d("aaaa", "로그인 실패 $error") TODO:
                    // 사용자가 취소
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    // 다른 오류
                    else {
                        UserApiClient.instance.loginWithKakaoAccount(
                            this,
                            callback = callback
                        ) // 카카오 이메일 로그인
                    }
                }
                // 로그인 성공 부분
                else if (token != null) {
                    Log.d("plgafhdflow","10")
                    //Log.d("aaaa", "로그인 성공 ${token.accessToken}") // TODO:
                }
            }
        }
        else {
            Log.d("plgafhdflow","11")
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback) // 카카오 이메일 로그인
        }
    }
}
