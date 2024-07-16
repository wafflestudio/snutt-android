package com.wafflestudio.snutt2.data.user

import android.webkit.CookieManager
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.SNUTTRestApiForGoogle
import com.wafflestudio.snutt2.lib.network.dto.*
import com.wafflestudio.snutt2.lib.toOptional
import com.wafflestudio.snutt2.lib.unwrap
import com.wafflestudio.snutt2.model.TableLectureCustom
import com.wafflestudio.snutt2.model.TableLectureCustomOptions
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.ui.ThemeMode
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val apiGoogle: SNUTTRestApiForGoogle,
    private val storage: SNUTTStorage,
    private val popupState: PopupState,
    externalScope: CoroutineScope,
) : UserRepository {

    override val user = storage.user.asStateFlow()
        .unwrap(externalScope)

    override val tableTrimParam: StateFlow<TableTrimParam> = storage.tableTrimParam.asStateFlow()

    override val tableLectureCustomOption: StateFlow<TableLectureCustom> =
        storage.tableLectureCustom.asStateFlow()

    override val accessToken = storage.accessToken.asStateFlow()

    override val themeMode = storage.themeMode.asStateFlow()

    override val compactMode = storage.compactMode.asStateFlow()

    override val firstBookmarkAlert = storage.firstBookmarkAlert.asStateFlow()

    override suspend fun postSignIn(id: String, password: String) {
        val response = api._postSignIn(PostSignInParams(id, password))
        storage.prefKeyUserId.update(response.userId.toOptional())
        storage.accessToken.update(response.token)
    }

    override suspend fun postLoginFacebook(facebookToken: String) {
        val response = api._postLoginFacebook(PostSocialLoginParams(facebookToken))
        storage.prefKeyUserId.update(response.userId.toOptional())
        storage.accessToken.update(response.token)
    }

    override suspend fun postLoginGoogle(googleAccessToken: String) {
        val response = api._postLoginGoogle(PostSocialLoginParams(googleAccessToken))
        storage.prefKeyUserId.update(response.userId.toOptional())
        storage.accessToken.update(response.token)
    }

    override suspend fun postLoginKakao(kakaoAccessToken: String) {
        val response = api._postLoginKakao(PostSocialLoginParams(kakaoAccessToken))
        storage.prefKeyUserId.update(response.userId.toOptional())
        storage.accessToken.update(response.token)
    }

    override suspend fun postSignUp(id: String, password: String, email: String) {
        val response = api._postSignUp(PostSignUpParams(id, password, email))
        storage.prefKeyUserId.update(response.userId.toOptional())
        storage.accessToken.update(response.token)
    }

    override suspend fun fetchUserInfo() {
        val response = api._getUserInfo()
        storage.user.update(response.toOptional())
    }

    override suspend fun patchUserInfo(nickname: String) {
        val response = api._patchUserInfo(PatchUserInfoParams(nickname))
        storage.user.update(response.toOptional())
    }

    override suspend fun deleteUserAccount() {
        api._deleteUserAccount()
        performLogout()
    }

    override suspend fun putUserPassword(
        oldPassword: String,
        newPassword: String,
    ) {
        val response = api._putUserPassword(
            PutUserPasswordParams(
                newPassword = newPassword,
                oldPassword = oldPassword,
            ),
        )
        storage.accessToken.update(response.token)
    }

    override suspend fun getUserFacebook(): GetUserFacebookResults {
        return api._getUserFacebook()
    }

    override suspend fun postUserPassword(id: String, password: String) {
        val response = api._postUserPassword(
            PostUserPasswordParams(
                id = id,
                password = password,
            ),
        )
        storage.accessToken.update(response.token)
    }

    override suspend fun deleteUserFacebook() {
        val response = api._deleteUserFacebook()
        storage.accessToken.update(response.token)
    }

    override suspend fun postUserFacebook(
        facebookId: String,
        facebookToken: String,
    ) {
        val response = api._postUserFacebook(
            PostUserFacebookParams(
                facebookId = facebookId,
                facebookToken = facebookToken,
            ),
        )
        storage.accessToken.update(response.token)
    }

    override suspend fun postFeedback(email: String, detail: String) {
        api._postFeedback(PostFeedbackParams(email = email, message = detail))
    }

    override suspend fun deleteFirebaseToken() {
        val firebaseToken = getFirebaseToken()
        api._deleteFirebaseToken(firebaseToken)
    }

    override suspend fun postForceLogout() {
        val firebaseToken = getFirebaseToken()
        val userId = storage.prefKeyUserId.get().value ?: return
        api._postForceLogout(
            PostForceLogoutParams(
                userId = userId,
                registrationId = firebaseToken,
            ),
        )
        performLogout()
    }

    override suspend fun getAccessToken(): String {
        return storage.accessToken.get()
    }

    override suspend fun setTableTrim(
        dayOfWeekFrom: Int?,
        dayOfWeekTo: Int?,
        hourFrom: Int?,
        hourTo: Int?,
        isAuto: Boolean?,
    ) {
        val prevTrimParam = storage.tableTrimParam.get()
        storage.tableTrimParam.update(
            TableTrimParam(
                dayOfWeekFrom = dayOfWeekFrom ?: prevTrimParam.dayOfWeekFrom,
                dayOfWeekTo = dayOfWeekTo ?: prevTrimParam.dayOfWeekTo,
                hourFrom = hourFrom ?: prevTrimParam.hourFrom,
                hourTo = hourTo ?: prevTrimParam.hourTo,
                forceFitLectures = isAuto ?: prevTrimParam.forceFitLectures,
            ),
        )
    }

    override suspend fun performLogout() {
        LoginManager.getInstance().logOut()
        storage.clearLoginScope()
        CookieManager.getInstance().removeAllCookies(null)
    }

    override suspend fun fetchAndSetPopup() {
        val popups = api._getPopup().popups.filter {
            val expireMillis: Long? = storage.shownPopupIdsAndTimestamp.get()[it.key]
            val currentMillis = System.currentTimeMillis()

            (expireMillis == null || currentMillis >= expireMillis)
        }
        popupState.popup = popups
    }

    override suspend fun closePopupWithHiddenDays() {
        val popup = popupState.popup.firstOrNull()
        if (popup != null) {
            val expiredDay: Long = popup.popupHideDays?.let { hideDays ->
                System.currentTimeMillis() + TimeUnit.DAYS.toMillis(hideDays.toLong())
            } ?: INFINITE_LONG_MILLIS

            storage.shownPopupIdsAndTimestamp.update(
                storage.shownPopupIdsAndTimestamp.get()
                    .toMutableMap()
                    .also {
                        it[popup.key] = expiredDay
                    },
            )

            popupState.popup = popupState.popup.drop(1)
        }
    }

    override suspend fun closePopup() {
        popupState.popup = popupState.popup.drop(1)
    }

    override suspend fun registerToken() {
        val token = getFirebaseToken()
        api._registerFirebaseToken(
            token,
            RegisterFirebaseTokenParams(),
        )
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        storage.themeMode.update(mode)
    }

    override suspend fun findIdByEmail(email: String) {
        api._postFindId(
            PostFindIdParams(email),
        )
    }

    override suspend fun checkEmailById(id: String): String {
        return api._postCheckEmailById(
            PostCheckEmailByIdParams(id),
        ).email
    }

    override suspend fun sendPwResetCodeToEmail(email: String) {
        api._postSendPwResetCodeToEmailById(
            PostSendPwResetCodeParams(email),
        )
    }

    override suspend fun verifyPwResetCode(id: String, code: String) {
        api._postVerifyCodeToResetPassword(
            PostVerifyPwResetCodeParams(id, code),
        )
    }

    override suspend fun resetPassword(id: String, password: String) {
        api._postResetPassword(
            PostResetPasswordParams(id, password),
        )
    }

    override suspend fun sendCodeToEmail(email: String) {
        api._postSendCodeToEmail(
            PostSendCodeToEmailParams(email),
        )
    }

    override suspend fun verifyEmailCode(code: String) {
        api._postVerifyEmailCode(
            PostVerifyEmailCodeParams(code),
        )
    }

    override suspend fun setCompactMode(compact: Boolean) {
        storage.compactMode.update(compact)
    }

    override suspend fun setTableLectureCustomOption(key: TableLectureCustomOptions, value: Boolean) {
        storage.tableLectureCustom.update(
            when (key){
                TableLectureCustomOptions.TITLE -> storage.tableLectureCustom.get().copy(title = value)
                TableLectureCustomOptions.PLACE -> storage.tableLectureCustom.get().copy(place = value)
                TableLectureCustomOptions.LECTURENUMBER -> storage.tableLectureCustom.get().copy(lectureNumber = value)
                TableLectureCustomOptions.INSTRUCTOR -> storage.tableLectureCustom.get().copy(instructor = value)
            }
        )
    }

    override suspend fun setFirstBookmarkAlertShown() {
        storage.firstBookmarkAlert.update(false)
    }

    override suspend fun getAccessTokenByAuthCode(authCode: String, clientId: String, clientSecret: String): String? {
        return apiGoogle._getAccessTokenByAuthCode(
            PostAccessTokenByAuthCodeParams(
                authCode = authCode,
                clientId = clientId,
                clientSecret = clientSecret,
            ),
        ).accessToken
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
                },
            )
        }
    }

    companion object {
        const val INFINITE_LONG_MILLIS = Long.MAX_VALUE
    }
}
