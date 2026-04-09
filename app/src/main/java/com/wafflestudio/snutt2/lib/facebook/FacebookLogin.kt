package com.wafflestudio.snutt2.lib.facebook

import android.content.Context
import androidx.activity.result.ActivityResultRegistryOwner
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.util.toast
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun facebookLogin(
    context: Context,
): LoginResult {
    val callbackManager = CallbackManager.Factory.create()
    val loginManager = LoginManager.getInstance()
    return suspendCancellableCoroutine { continuation ->
        val callback = object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                continuation.resume(result) { _, _, _ ->
                    loginManager.unregisterCallback(callbackManager)
                }
            }

            override fun onCancel() {
                context.toast(context.getString(R.string.sign_in_facebook_failed_cancelled))
                continuation.cancel()
            }

            override fun onError(error: FacebookException) {
                context.toast(context.getString(R.string.sign_in_facebook_failed_unknown))
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
