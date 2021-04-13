package com.wafflestudio.snutt2.manager

import android.util.Log
import com.facebook.login.LoginManager
import com.google.firebase.iid.FirebaseInstanceId
import com.wafflestudio.snutt2.SNUTTApplication
import com.wafflestudio.snutt2.model.User
import com.wafflestudio.snutt2.network.dto.*
import com.wafflestudio.snutt2.network.dto.core.TempUtil
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

/**
 * Created by makesource on 2016. 1. 16..
 */
class UserManager private constructor(private val app: SNUTTApplication) {
    // /////
    var user: User
        private set

    // login with local id
    fun postSignIn(id: String, password: String): Single<PostSignInResults> {
        return app.restService!!.postSignIn(PostSignInParams(id, password))
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                Log.d(TAG, "post local sign in success!!")
                Log.d(TAG, "token : " + result.token + " user_id : " + result.userId)
                PrefManager.instance!!.prefKeyXAccessToken = result.token
                PrefManager.instance!!.prefKeyUserId = result.userId
                // Refactoring FIXME: chaining FlatMap
                instance!!.registerFirebaseToken().subscribe()
                logUserTemp(result.userId)
            }
            .doOnError {
                Log.w(TAG, "post local sign in failed!")
            }
    }

    // login with facebook id
    fun postLoginFacebook(facebookId: String, facebookToken: String): Single<PostLoginFacebookResults> {
        return app.restService!!.postLoginFacebook(PostLoginFacebookParams(facebookId, facebookToken))
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                Log.d(TAG, "post user facebook success!")
                Log.d(TAG, "token : " + result.token + " user_id : " + result.userId)
                PrefManager.instance!!.prefKeyXAccessToken = result.token
                PrefManager.instance!!.prefKeyUserId = result.userId
                // Refactoring FIXME: chaining FlatMap
                instance!!.registerFirebaseToken().subscribe()
                logUserTemp(result.userId)
            }
            .doOnError {
                Log.w(TAG, "post user facebook failed!")
            }
    }

    fun postSingUp(id: String, password: String, email: String): Single<PostSignUpResults> {
        // id, password -> regex check!
        return app.restService!!.postSignUp(PostSignUpParams(id, password, email))
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                Log.d(TAG, "post sign up success!")
                Log.d(TAG, "token : " + result.token + " user_id : " + result.userId)
                PrefManager.instance!!.prefKeyXAccessToken = result.token
                PrefManager.instance!!.prefKeyUserId = result.userId
                // Refactoring FIXME: chaining FlatMap
                instance!!.registerFirebaseToken().subscribe()
                logUserTemp(result.userId)
            }
    }

    fun getUserInfo(): Single<User> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        return app.restService!!.getUserInfo(token!!)
            .map { TempUtil.toLegacyModel(it) }
            .doOnSuccess {
                Log.d(TAG, "get user info success")
                this@UserManager.user = it
            }
            .doOnError {
                Log.d(TAG, "get user info failed")
            }
    }

    fun putUserInfo(email: String): Single<PutUserInfoResults> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        return app.restService!!.putUserInfo(
            token!!,
            PutUserInfoParams(email))
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "put user info success")
                user.email = email
            }
            .doOnError {
                Log.d(TAG, "put user info failed")
            }
    }

    fun deleteUserAccount(): Single<DeleteUserAccountResults> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        return app.restService!!.deleteUserAccount(token!!)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "delete user account success")
            }
            .doOnError {
                Log.d(TAG, "get delete user account failed")
            }
    }

    fun putUserPassword(oldPassword: String, newPassword: String): Single<PutUserPasswordResults> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val param = PutUserPasswordParams(
            newPassword = newPassword,
            oldPassword = oldPassword
        )

        return app.restService!!.putUserPassword(token!!, param)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "put user password success")
                PrefManager.instance!!.prefKeyXAccessToken = it.token
            }
            .doOnError {
                Log.d(TAG, "put user password failed")
            }
    }

    fun getUserFacebook(): Single<GetUserFacebookResults> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        return app.restService!!.getUserFacebook(token!!)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get user facebook success!")
            }
            .doOnError {
                Log.w(TAG, "get user facebook failed")
            }
    }

    // 새로운 local_id 추가
    fun postUserPassword(id: String, password: String): Single<PostUserPasswordResults> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val param = PostUserPasswordParams(
            id = id,
            password = password
        )
        return app.restService!!.postUserPassword(token!!, param)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "post user password success!")
                PrefManager.instance!!.prefKeyXAccessToken = it.token
            }
            .doOnError {
                Log.w(TAG, "post user password failed")
            }
    }

    fun deleteUserFacebook(): Single<DeleteUserFacebookResults> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        return app.restService!!.deleteUserFacebook(token!!)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "delete user facebook success!")
                PrefManager.instance!!.prefKeyXAccessToken = it.token
            }
            .doOnError {
                Log.w(TAG, "delete user facebook failed")
            }
    }

    // facebook 계정 연동
    fun postUserFacebook(facebookId: String, facebookToken: String): Single<PostUserFacebookResults> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val param = PostUserFacebookParams(
            facebookId = facebookId,
            facebookToken = facebookToken
        )
        return app.restService!!.postUserFacebook(token!!, param)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "post user facebook success!")
                PrefManager.instance!!.prefKeyXAccessToken = it.token
            }
            .doOnError {
                Log.w(TAG, "post user facebook failed")
            }
    }

    fun getAppVersion(): Single<GetAppVersionResults> {
        return app.restService!!.getAppVersion()
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get app version success!")
            }
            .doOnError {
                Log.w(TAG, "get app version failed")
            }
    }

    fun postFeedback(email: String, detail: String): Single<PostFeedbackResults> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val param = PostFeedbackParams(
            email = email,
            message = detail
        )
        return app.restService!!.postFeedback(
            token!!,
            param
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "post feedback success!")
            }
            .doOnError {
                Log.w(TAG, "post feedback failed")
            }
    }

    fun registerFirebaseToken(): Single<RegisterFirebaseTokenResults> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val firebaseToken = FirebaseInstanceId.getInstance().token
        return app.restService!!.registerFirebaseToken(token!!, firebaseToken!!, RegisterFirebaseTokenParams())
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "register firebase token success!")
                Log.d(TAG, "token : $firebaseToken")
            }
            .doOnError {
                Log.w(TAG, "register firebase token failed.")
            }
    }

    fun deleteFirebaseToken(): Single<DeleteFirebaseTokenResults> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val firebaseToken = FirebaseInstanceId.getInstance().token
        return app.restService!!.deleteFirebaseToken(token!!, firebaseToken!!)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "delete firebase token success!")
                Log.d(TAG, "token : $firebaseToken")
            }
            .doOnError {
                Log.w(TAG, "delete firebase token failed.")
            }
    }

    fun postForceLogout(): Single<PostForceLogoutResults> {
        val user_id: String? = PrefManager.instance!!.prefKeyUserId
        val firebaseToken = FirebaseInstanceId.getInstance().token
        val param = PostForceLogoutParams(
            userId = user_id!!,
            registrationId = firebaseToken!!
        )
        return app.restService!!.postForceLogout(param)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "post force logout success")
            }
            .doOnError {
                Log.w(TAG, "post force logout failed..")
            }
    }

    fun performLogout() {
        /* firebase token 삭제 후 로그아웃 시행 */
        LectureManager.instance!!.reset()
        NotiManager.instance!!.reset()
        TableManager.instance!!.reset()
        TagManager.instance!!.reset()
        PrefManager.instance!!.resetPrefValue()
        LoginManager.getInstance().logOut() // for facebook sdk
        user = User()
    }

    // Refactor FIXME
    private fun logUserTemp(id: String?) {
        // do nothing
    }

    companion object {
        private const val TAG = "USER_MANAGER"
        private var singleton: UserManager? = null
        fun getInstance(app: SNUTTApplication): UserManager? {
            if (singleton == null) {
                singleton = UserManager(app)
            }
            return singleton
        }

        @JvmStatic
        val instance: UserManager?
            get() {
                if (singleton == null) Log.e(TAG, "This method should not be called at this time!!")
                return singleton
            }
    }

    /**
     * UserManager 싱글톤
     */
    init {
        user = User()
    }
}
