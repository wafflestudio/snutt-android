package com.wafflestudio.snutt2.lib

import android.content.Context
import androidx.activity.result.ActivityResultRegistryOwner
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.android.toast
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun facebookLogin(
    context: Context,
): LoginResult {
    val callbackManager = CallbackManager.Factory.create()
    val loginManager = LoginManager.getInstance()
    return suspendCancellableCoroutine { continuation ->
        val callback = object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                continuation.resume(
                    result,
                    onCancellation = { loginManager.unregisterCallback(callbackManager) },
                )
            }

            override fun onCancel() {
                context.toast(context.getString(R.string.sign_up_facebook_login_failed_toast))
                continuation.cancel()
            }

            override fun onError(error: FacebookException) {
                context.toast(context.getString(R.string.sign_up_facebook_login_failed_toast))
                continuation.cancel(error)
            }
        }

        loginManager.registerCallback(callbackManager, callback)

        loginManager.logInWithReadPermissions(
            context as ActivityResultRegistryOwner,
            callbackManager,
            emptyList(),
        )
        continuation.invokeOnCancellation {
            loginManager.unregisterCallback(callbackManager)
        }
    }
}
