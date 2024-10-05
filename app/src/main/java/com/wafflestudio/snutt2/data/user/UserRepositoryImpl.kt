package com.wafflestudio.snutt2.data.user

import android.webkit.CookieManager
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.wafflestudio.snutt2.core.database.model.User
import com.wafflestudio.snutt2.core.database.preference.SNUTTStorageTemp
import com.wafflestudio.snutt2.core.database.util.map
import com.wafflestudio.snutt2.core.database.util.toOptional
import com.wafflestudio.snutt2.core.database.util.unwrap
import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSource
import com.wafflestudio.snutt2.core.network.model.GetUserFacebookResults
import com.wafflestudio.snutt2.core.network.model.PostAccessTokenByAuthCodeParams
import com.wafflestudio.snutt2.core.network.model.PostSocialLoginParams
import com.wafflestudio.snutt2.core.network.retrofit.google.SNUTTRestApiForGoogle
import com.wafflestudio.snutt2.lib.network.dto.core.SocialProvidersCheckDto
import com.wafflestudio.snutt2.lib.network.dto.core.toDatabaseModel
import com.wafflestudio.snutt2.lib.network.dto.core.toExternalModel
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.model.toDatabaseModel
import com.wafflestudio.snutt2.model.toExternalModel
import com.wafflestudio.snutt2.ui.ThemeMode
import com.wafflestudio.snutt2.ui.toDatabaseModel
import com.wafflestudio.snutt2.ui.toExternalModel
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.wafflestudio.snutt2.core.database.model.TableTrimParam as TableTrimParamDatabase
import com.wafflestudio.snutt2.core.database.model.ThemeMode as ThemeModeDatabase
import com.wafflestudio.snutt2.core.network.model.PatchUserInfoParams as PatchUserInfoParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PostCheckEmailByIdParams as PostCheckEmailByIdParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PostFeedbackParams as PostFeedbackParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PostFindIdParams as PostFindIdParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PostForceLogoutParams as PostForceLogoutParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PostResetPasswordParams as PostResetPasswordParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PostSendCodeToEmailParams as PostSendCodeToEmailParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PostSendPwResetCodeParams as PostSendPwResetCodeParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PostSignInParams as PostSignInParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PostSignUpParams as PostSignUpParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PostUserFacebookParams as PostUserFacebookParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PostUserPasswordParams as PostUserPasswordParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PostVerifyEmailCodeParams as PostVerifyEmailCodeParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PostVerifyPwResetCodeParams as PostVerifyPwResetCodeParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PutUserPasswordParams as PutUserPasswordParamsNetwork
import com.wafflestudio.snutt2.core.network.model.RegisterFirebaseTokenParams as RegisterFirebaseTokenParamsNetwork

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: SNUTTNetworkDataSource,
    private val storage: SNUTTStorageTemp,
    private val apiGoogle: SNUTTRestApiForGoogle,
    private val popupState: PopupState,
    externalScope: CoroutineScope,
) : UserRepository {

    override val user = storage.user.asStateFlow()
        .unwrap(externalScope).map(externalScope) { it: User? -> it?.toExternalModel() } // TODO : 이게 맞나..? 싶어서 이런 부분은 다 TODO 달아놓음

    override val tableTrimParam: StateFlow<TableTrimParam> = storage.tableTrimParam.asStateFlow()
        .map(externalScope) { it: TableTrimParamDatabase -> it.toExternalModel() } // TODO : database 변환 사용 부분

    override val accessToken = storage.accessToken.asStateFlow()

    override val themeMode = storage.themeMode.asStateFlow()
        .map(externalScope) { it: ThemeModeDatabase -> it.toExternalModel() } // TODO : database 변환 사용 부분

    override val compactMode = storage.compactMode.asStateFlow()

    override val firstBookmarkAlert = storage.firstBookmarkAlert.asStateFlow()

    override suspend fun postSignIn(id: String, password: String) {
        val response = api._postSignIn(PostSignInParamsNetwork(id, password))
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
        val response = api._postSignUp(PostSignUpParamsNetwork(id, password, email))
        storage.prefKeyUserId.update(response.userId.toOptional())
        storage.accessToken.update(response.token)
    }

    override suspend fun fetchUserInfo() {
        val response = api._getUserInfo().toExternalModel()
        storage.user.update(response.toDatabaseModel().toOptional()) // TODO : database 변환 사용 부분
    }

    override suspend fun patchUserInfo(nickname: String) {
        val response = api._patchUserInfo(PatchUserInfoParamsNetwork(nickname)).toExternalModel()
        storage.user.update(response.toDatabaseModel().toOptional()) // TODO : database 변환 사용 부분
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
            PutUserPasswordParamsNetwork(
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
            PostUserPasswordParamsNetwork(
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
            PostUserFacebookParamsNetwork(
                facebookId = facebookId,
                facebookToken = facebookToken,
            ),
        )
        storage.accessToken.update(response.token)
    }

    override suspend fun postFeedback(email: String, detail: String) {
        api._postFeedback(PostFeedbackParamsNetwork(email = email, message = detail))
    }

    override suspend fun deleteFirebaseToken() {
        val firebaseToken = getFirebaseToken()
        api._deleteFirebaseToken(firebaseToken)
    }

    override suspend fun postForceLogout() {
        val firebaseToken = getFirebaseToken()
        val userId = storage.prefKeyUserId.get().value ?: return
        api._postForceLogout(
            PostForceLogoutParamsNetwork(
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
        val prevTrimParam = storage.tableTrimParam.get().toExternalModel()
        storage.tableTrimParam.update(
            TableTrimParam(
                dayOfWeekFrom = dayOfWeekFrom ?: prevTrimParam.dayOfWeekFrom,
                dayOfWeekTo = dayOfWeekTo ?: prevTrimParam.dayOfWeekTo,
                hourFrom = hourFrom ?: prevTrimParam.hourFrom,
                hourTo = hourTo ?: prevTrimParam.hourTo,
                forceFitLectures = isAuto ?: prevTrimParam.forceFitLectures,
            ).toDatabaseModel(), // TODO : database 변환 사용 부분
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
            RegisterFirebaseTokenParamsNetwork(),
        )
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        storage.themeMode.update(mode.toDatabaseModel()) // TODO : database 변환 사용 부분
    }

    override suspend fun findIdByEmail(email: String) {
        api._postFindId(
            PostFindIdParamsNetwork(email),
        )
    }

    override suspend fun checkEmailById(id: String): String {
        return api._postCheckEmailById(
            PostCheckEmailByIdParamsNetwork(id),
        ).email
    }

    override suspend fun sendPwResetCodeToEmail(email: String) {
        api._postSendPwResetCodeToEmailById(
            PostSendPwResetCodeParamsNetwork(email),
        )
    }

    override suspend fun verifyPwResetCode(id: String, code: String) {
        api._postVerifyCodeToResetPassword(
            PostVerifyPwResetCodeParamsNetwork(id, code),
        )
    }

    override suspend fun resetPassword(id: String, password: String) {
        api._postResetPassword(
            PostResetPasswordParamsNetwork(id, password),
        )
    }

    override suspend fun sendCodeToEmail(email: String) {
        api._postSendCodeToEmail(
            PostSendCodeToEmailParamsNetwork(email),
        )
    }

    override suspend fun verifyEmailCode(code: String) {
        api._postVerifyEmailCode(
            PostVerifyEmailCodeParamsNetwork(code),
        )
    }

    override suspend fun setCompactMode(compact: Boolean) {
        storage.compactMode.update(compact)
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

    override suspend fun getSocialProviders(): SocialProvidersCheckDto {
        return api._getSocialProviders().toExternalModel()
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
