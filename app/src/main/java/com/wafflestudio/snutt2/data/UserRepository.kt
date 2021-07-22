package com.wafflestudio.snutt2.data

import com.facebook.login.LoginManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.*
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.lib.toOptional
import com.wafflestudio.snutt2.manager.PrefStorage
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by makesource on 2016. 1. 16..
 */
@Singleton
class UserRepository @Inject constructor(
    private val snuttRestApi: SNUTTRestApi,
    private val snuttStorage: SNUTTStorage,
    private val prefStorage: PrefStorage
) {
    val user: Observable<UserDto> = snuttStorage.user
        .asObservable()
        .filterEmpty()

    // login with local id
    fun postSignIn(id: String, password: String): Single<*> {
        return snuttRestApi.postSignIn(PostSignInParams(id, password))
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                snuttStorage.accessToken.setValue(result.token)
                snuttStorage.prefKeyUserId.setValue(result.userId.toOptional())
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
                snuttStorage.accessToken.setValue(result.token)
                snuttStorage.prefKeyUserId.setValue(result.userId.toOptional())
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
                snuttStorage.accessToken.setValue(result.token)
                snuttStorage.prefKeyUserId.setValue(result.userId.toOptional())
            }
            .flatMap {
                registerFirebaseToken()
            }
    }

    fun fetchUserInfo(): Single<UserDto> {
        return snuttRestApi.getUserInfo()
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                snuttStorage.user.setValue(it.toOptional())
            }
    }

    fun putUserInfo(email: String): Single<PutUserInfoResults> {
        return snuttRestApi.putUserInfo(
            PutUserInfoParams(email)
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                snuttStorage.user.setValue(
                    snuttStorage.user.getValue().get()?.copy(email = email).toOptional()
                )
            }
    }

    fun deleteUserAccount(): Single<DeleteUserAccountResults> {
        return snuttRestApi.deleteUserAccount()
            .subscribeOn(Schedulers.io())
    }

    fun putUserPassword(oldPassword: String, newPassword: String): Single<PutUserPasswordResults> {
        val param = PutUserPasswordParams(
            newPassword = newPassword,
            oldPassword = oldPassword
        )

        return snuttRestApi.putUserPassword(param)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                snuttStorage.accessToken.setValue(result.token)
            }
    }

    fun getUserFacebook(): Single<GetUserFacebookResults> {
        return snuttRestApi.getUserFacebook()
            .subscribeOn(Schedulers.io())
    }

    // 새로운 local_id 추가
    fun postUserPassword(id: String, password: String): Single<PostUserPasswordResults> {
        val param = PostUserPasswordParams(
            id = id,
            password = password
        )
        return snuttRestApi.postUserPassword(param)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                snuttStorage.accessToken.setValue(result.token)
            }
    }

    fun deleteUserFacebook(): Single<DeleteUserFacebookResults> {
        return snuttRestApi.deleteUserFacebook()
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                snuttStorage.accessToken.setValue(result.token)
            }
    }

    // facebook 계정 연동
    fun postUserFacebook(
        facebookId: String,
        facebookToken: String
    ): Single<PostUserFacebookResults> {
        val param = PostUserFacebookParams(
            facebookId = facebookId,
            facebookToken = facebookToken
        )
        return snuttRestApi.postUserFacebook(param)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                snuttStorage.accessToken.setValue(result.token)
            }
    }

    fun postFeedback(email: String, detail: String): Single<PostFeedbackResults> {
        val param = PostFeedbackParams(
            email = email,
            message = detail
        )
        return snuttRestApi.postFeedback(
            param
        )
            .subscribeOn(Schedulers.io())
    }

    fun deleteFirebaseToken(): Single<DeleteFirebaseTokenResults> {
        return getFirebaseToken().flatMap { firebaseToken ->
            snuttRestApi.deleteFirebaseToken(firebaseToken)
        }
            .subscribeOn(Schedulers.io())
    }

    fun postForceLogout(): Single<PostForceLogoutResults> {
        return getFirebaseToken().flatMap { firebaseToken ->
            val user_id: String? = prefStorage.prefKeyUserId
            val param = PostForceLogoutParams(
                userId = user_id ?: "",
                registrationId = firebaseToken
            )
            snuttRestApi.postForceLogout(param)
        }
            .subscribeOn(Schedulers.io())
            .doOnTerminate {
                performLogout()
            }
    }

    fun performLogout() {
        /* firebase token 삭제 후 로그아웃 시행 */
        LoginManager.getInstance().logOut() // for facebook sdk
        snuttStorage.clearAll()
    }

    private fun registerFirebaseToken(): Single<RegisterFirebaseTokenResults> {
        return getFirebaseToken().flatMap { firebaseToken ->
            snuttRestApi.registerFirebaseToken(
                firebaseToken,
                RegisterFirebaseTokenParams()
            )
        }
            .subscribeOn(Schedulers.io())
    }

    private fun getFirebaseToken(): Single<String> {
        return Single.create {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    it.onError(RuntimeException("cannot get firebase token"))
                    return@OnCompleteListener
                }
                val token = task.result
                it.onSuccess(token)
            })
        }
    }
}
