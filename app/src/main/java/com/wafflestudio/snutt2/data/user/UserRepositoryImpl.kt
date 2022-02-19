package com.wafflestudio.snutt2.data.user

import androidx.datastore.core.DataStore
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.*
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.lib.storage.UserPreferences
import com.wafflestudio.snutt2.model.TableTrimParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val userStore: DataStore<UserPreferences>
) : UserRepository {

    override val user: Flow<UserDto> = userStore.data
        .map { it.data }
        .filterNotNull()

    override val tableTrimParam: Flow<TableTrimParam> = userStore.data
        .map { it.tableTrimParam }
        .filterNotNull()

    override suspend fun postSignIn(id: String, password: String) {
        userStore.updateData { prev ->
            val response = api._postSignIn(PostSignInParams(id, password))
            prev.copy(userId = response.userId, accessToken = response.token)
        }
        registerFirebaseToken()
    }

    override suspend fun postLoginFacebook(facebookId: String, facebookToken: String) {
        userStore.updateData { prev ->
            val response =
                api._postLoginFacebook(PostLoginFacebookParams(facebookId, facebookToken))
            prev.copy(userId = response.userId, accessToken = response.token)
        }
    }

    override suspend fun postSingUp(id: String, password: String, email: String) {
        userStore.updateData { prev ->
            val response = api._postSignUp(PostSignUpParams(id, password, email))
            prev.copy(userId = response.userId, accessToken = response.token)
        }
    }

    override suspend fun fetchUserInfo() {
        userStore.updateData { prev ->
            val response = api._getUserInfo()
            prev.copy(data = response)
        }
    }

    override suspend fun putUserInfo(email: String) {
        userStore.updateData { prev ->
            api._putUserInfo(PutUserInfoParams(email))
            prev.data?.let {
                prev.copy(
                    data = it.copy(email = email)
                )
            } ?: prev
        }
    }

    override suspend fun deleteUserAccount() {
        api._deleteUserAccount()
        performLogout()
    }

    override suspend fun putUserPassword(
        oldPassword: String,
        newPassword: String
    ) {
        userStore.updateData { prev ->
            val response = api._putUserPassword(
                PutUserPasswordParams(
                    newPassword = newPassword,
                    oldPassword = oldPassword
                )
            )
            prev.copy(accessToken = response.token)
        }
    }

    override suspend fun getUserFacebook(): GetUserFacebookResults {
        return api._getUserFacebook()
    }

    override suspend fun postUserPassword(id: String, password: String) {
        userStore.updateData { prev ->
            val response = api._postUserPassword(
                PostUserPasswordParams(
                    id = id,
                    password = password
                )
            )
            prev.copy(accessToken = response.token)
        }
    }

    override suspend fun deleteUserFacebook() {
        userStore.updateData { prev ->
            val response = api._deleteUserFacebook()
            prev.copy(accessToken = response.token)
        }
    }

    override suspend fun postUserFacebook(
        facebookId: String,
        facebookToken: String
    ) {
        userStore.updateData { prev ->
            val response = api._postUserFacebook(
                PostUserFacebookParams(
                    facebookId = facebookId,
                    facebookToken = facebookToken
                )
            )
            prev.copy(accessToken = response.token)
        }
    }

    override suspend fun postFeedback(email: String, detail: String) {
        api._postFeedback(PostFeedbackParams(email = email, message = detail))
    }

    override suspend fun deleteFirebaseToken() {
        val firebaseToken = getFirebaseToken()
        api.deleteFirebaseToken(firebaseToken)
    }

    override suspend fun postForceLogout() {
        val firebaseToken = getFirebaseToken()
        val userId = userStore.data.first().userId
        api._postForceLogout(
            PostForceLogoutParams(
                userId = userId,
                registrationId = firebaseToken
            )
        )
        performLogout()
    }

    private suspend fun performLogout() {
        LoginManager.getInstance().logOut()
        userStore.updateData {
            UserPreferences()
        }
    }

    private suspend fun registerFirebaseToken() {
        val token = getFirebaseToken()
        api._registerFirebaseToken(
            token,
            RegisterFirebaseTokenParams()
        )
    }

    private suspend fun getFirebaseToken(): String {
        return suspendCoroutine { cont ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener(
                OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        cont.resumeWithException(RuntimeException("cannot get firebase token"))
                        return@OnCompleteListener
                    }
                    val token = task.result
                    cont.resume(token!!)
                }
            )
        }
    }
}
