package com.wafflestudio.snutt2.data.user

import android.util.Log
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

    override val popupTimeStamp: Flow<Map<String, Long>> = userStore.data
        .map { it.popupTimeStamp }
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

    override suspend fun postSignUp(id: String, password: String, email: String) {
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

    override suspend fun getAccessToken(): String {
        return userStore.data.first().accessToken
    }

    override fun accessToken(): Flow<String> {
        return userStore.data.map { it.accessToken }
    }

    override suspend fun setTableTrim(
        dayOfWeekFrom: Int?,
        dayOfWeekTo: Int?,
        hourFrom: Int?,
        hourTo: Int?,
        isAuto: Boolean?
    ) {
        userStore.updateData { prev ->
            val prevTrimParam = prev.tableTrimParam
            prev.copy(
                tableTrimParam = TableTrimParam(
                    dayOfWeekFrom = dayOfWeekFrom ?: prevTrimParam.dayOfWeekFrom,
                    dayOfWeekTo = dayOfWeekTo ?: prevTrimParam.dayOfWeekTo,
                    hourFrom = hourFrom ?: prevTrimParam.hourFrom,
                    hourTo = hourTo ?: prevTrimParam.hourTo,
                    forceFitLectures = isAuto ?: prevTrimParam.forceFitLectures,
                )
            )
        }
    }

    override suspend fun performLogout() {
        LoginManager.getInstance().logOut()
        userStore.updateData {
            UserPreferences()
        }
    }

    override suspend fun updatePopupTimeStamp(key: String, value: Long) {
        userStore.updateData { prev ->
            val prevTimeStamp = prev.popupTimeStamp.toMutableMap()
            prevTimeStamp[key] = value
            prev.copy(
                popupTimeStamp = prevTimeStamp.toMap()
            )
        }
    }

    override suspend fun getPopup(): GetPopupResults {
        Log.d("aaaa", "popup api")
        return GetPopupResults(
            popups = listOf(GetPopupResults.Popup(
                key = "1236",
                url = "https://postfiles.pstatic.net/MjAyMjA5MjBfOCAg/MDAxNjYzNjYwNTY4NjQ4.ACAqF1MNObyk_PJRvxzOZdgCS68sqiZiRWpErr2N8twg.FnCSqgRkRgA0Ji7zK0zF7jpCGWmSPh3PkHjvnCHGsOQg.JPEG.naver_diary/2_네이버코드_한글의_역사_매거진.JPG?type=w773",
                popupHideDays = 1,
            ))
        )
//        return api._getPopup()
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
