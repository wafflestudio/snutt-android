package com.wafflestudio.snutt2.manager

import android.util.Log
import com.facebook.login.LoginManager
import com.google.firebase.iid.FirebaseInstanceId
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.*
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by makesource on 2016. 1. 16..
 */
@Singleton
class UserManager @Inject constructor(
    private val snuttRestApi: SNUTTRestApi,
    private val lectureManager: LectureManager,
    private val notiManager: NotiManager,
    private val tableManager: TableManager,
    private val tagManager: TagManager,
    private val prefStorage: PrefStorage
) {
    // /////
    var user: UserDto = UserDto()
        private set

    // login with local id
    fun postSignIn(id: String, password: String): Single<*> {
        return snuttRestApi.postSignIn(PostSignInParams(id, password))
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                Log.d(TAG, "post local sign in success!!")
                Log.d(TAG, "token : " + result.token + " user_id : " + result.userId)
                prefStorage.prefKeyXAccessToken = result.token
                prefStorage.prefKeyUserId = result.userId
            }
            .doOnError {
                Log.w(TAG, "post local sign in failed!")
            }
            .flatMap {
                registerFirebaseToken()
            }
    }

    // login with facebook id
    fun postLoginFacebook(facebookId: String, facebookToken: String): Single<*> {
        return snuttRestApi.postLoginFacebook(PostLoginFacebookParams(facebookId, facebookToken))
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                Log.d(TAG, "post user facebook success!")
                Log.d(TAG, "token : " + result.token + " user_id : " + result.userId)
                prefStorage.prefKeyXAccessToken = result.token
                prefStorage.prefKeyUserId = result.userId
            }
            .doOnError {
                Log.w(TAG, "post user facebook failed!")
            }
            .flatMap {
                registerFirebaseToken()
            }
    }

    fun postSingUp(id: String, password: String, email: String): Single<*> {
        // id, password -> regex check!
        return snuttRestApi.postSignUp(PostSignUpParams(id, password, email))
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                Log.d(TAG, "post sign up success!")
                Log.d(TAG, "token : " + result.token + " user_id : " + result.userId)
                prefStorage.prefKeyXAccessToken = result.token
                prefStorage.prefKeyUserId = result.userId
            }
            .flatMap {
                registerFirebaseToken()
            }
    }

    fun getUserInfo(): Single<UserDto> {
        val token: String? = prefStorage.prefKeyXAccessToken
        return snuttRestApi.getUserInfo(token!!)
            .doOnSuccess {
                Log.d(TAG, "get user info success")
                this@UserManager.user = it
            }
            .doOnError {
                Log.d(TAG, "get user info failed")
            }
    }

    fun putUserInfo(email: String): Single<PutUserInfoResults> {
        val token: String? = prefStorage.prefKeyXAccessToken
        return snuttRestApi.putUserInfo(
            token!!,
            PutUserInfoParams(email)
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "put user info success")
                user = user.copy(email = email)
            }
            .doOnError {
                Log.d(TAG, "put user info failed")
            }
    }

    fun deleteUserAccount(): Single<DeleteUserAccountResults> {
        val token: String? = prefStorage.prefKeyXAccessToken
        return snuttRestApi.deleteUserAccount(token!!)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "delete user account success")
            }
            .doOnError {
                Log.d(TAG, "get delete user account failed")
            }
    }

    fun putUserPassword(oldPassword: String, newPassword: String): Single<PutUserPasswordResults> {
        val token: String? = prefStorage.prefKeyXAccessToken
        val param = PutUserPasswordParams(
            newPassword = newPassword,
            oldPassword = oldPassword
        )

        return snuttRestApi.putUserPassword(token!!, param)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "put user password success")
                prefStorage.prefKeyXAccessToken = it.token
            }
            .doOnError {
                Log.d(TAG, "put user password failed")
            }
    }

    fun getUserFacebook(): Single<GetUserFacebookResults> {
        val token: String? = prefStorage.prefKeyXAccessToken
        return snuttRestApi.getUserFacebook(token!!)
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
        val token: String? = prefStorage.prefKeyXAccessToken
        val param = PostUserPasswordParams(
            id = id,
            password = password
        )
        return snuttRestApi.postUserPassword(token!!, param)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "post user password success!")
                prefStorage.prefKeyXAccessToken = it.token
            }
            .doOnError {
                Log.w(TAG, "post user password failed")
            }
    }

    fun deleteUserFacebook(): Single<DeleteUserFacebookResults> {
        val token: String? = prefStorage.prefKeyXAccessToken
        return snuttRestApi.deleteUserFacebook(token!!)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "delete user facebook success!")
                prefStorage.prefKeyXAccessToken = it.token
            }
            .doOnError {
                Log.w(TAG, "delete user facebook failed")
            }
    }

    // facebook 계정 연동
    fun postUserFacebook(facebookId: String, facebookToken: String): Single<PostUserFacebookResults> {
        val token: String? = prefStorage.prefKeyXAccessToken
        val param = PostUserFacebookParams(
            facebookId = facebookId,
            facebookToken = facebookToken
        )
        return snuttRestApi.postUserFacebook(token!!, param)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "post user facebook success!")
                prefStorage.prefKeyXAccessToken = it.token
            }
            .doOnError {
                Log.w(TAG, "post user facebook failed")
            }
    }

    fun getAppVersion(): Single<GetAppVersionResults> {
        return snuttRestApi.getAppVersion()
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get app version success!")
            }
            .doOnError {
                Log.w(TAG, "get app version failed")
            }
    }

    fun postFeedback(email: String, detail: String): Single<PostFeedbackResults> {
        val token: String? = prefStorage.prefKeyXAccessToken
        val param = PostFeedbackParams(
            email = email,
            message = detail
        )
        return snuttRestApi.postFeedback(
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
        val token: String? = prefStorage.prefKeyXAccessToken
        val firebaseToken = FirebaseInstanceId.getInstance().token
        return snuttRestApi.registerFirebaseToken(token!!, firebaseToken!!, RegisterFirebaseTokenParams())
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
        val token: String? = prefStorage.prefKeyXAccessToken
        val firebaseToken = FirebaseInstanceId.getInstance().token
        return snuttRestApi.deleteFirebaseToken(token!!, firebaseToken!!)
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
        val user_id: String? = prefStorage.prefKeyUserId
        val firebaseToken = FirebaseInstanceId.getInstance().token
        val param = PostForceLogoutParams(
            userId = user_id ?: "",
            registrationId = firebaseToken ?: ""
        )
        return snuttRestApi.postForceLogout(param)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "post force logout success")
            }
            .doOnError {
                Log.w(TAG, "post force logout failed..")
            }
            .doOnTerminate {
                performLogout()
            }
    }

    fun performLogout() {
        /* firebase token 삭제 후 로그아웃 시행 */
        lectureManager.reset()
        notiManager.reset()
        tableManager.reset()
        tagManager.reset()
        prefStorage.resetPrefValue()
        LoginManager.getInstance().logOut() // for facebook sdk
        user = UserDto()
    }

    companion object {
        private const val TAG = "USER_MANAGER"
    }
}
