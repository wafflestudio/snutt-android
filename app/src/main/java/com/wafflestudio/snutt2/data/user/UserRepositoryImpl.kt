package com.wafflestudio.snutt2.data.user

import android.webkit.CookieManager
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.PushPreferences
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.domainmodel.User
import com.wafflestudio.snutt2.domainmodel.toDataModel
import com.wafflestudio.snutt2.domainmodel.toNetworkModel
import com.wafflestudio.snutt2.lib.map
import com.wafflestudio.snutt2.lib.network.dto.core.toDomainModel
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.SNUTTRestApiForGoogle
import com.wafflestudio.snutt2.lib.network.dto.*
import com.wafflestudio.snutt2.lib.network.Unknown
import com.wafflestudio.snutt2.lib.network.toDomainError
import com.wafflestudio.snutt2.lib.preferences.model.toDomainModel
import com.wafflestudio.snutt2.lib.toOptional
import com.wafflestudio.snutt2.lib.unwrap
import com.wafflestudio.snutt2.ui.ThemeMode
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
    externalScope: CoroutineScope,
) : UserRepository {

    override val user: StateFlow<User?> = storage.user.asStateFlow()
        .unwrap(externalScope)
        .map(externalScope) { it?.toDomainModel() }

    override val tableTrimParam: StateFlow<TableTrimParam> = storage.tableTrimParam.asStateFlow().map(externalScope) {
        it.toDomainModel()
    }

    override val tableLectureCustomOption: StateFlow<TableLectureCustom> =
        storage.tableLectureCustom.asStateFlow().map(externalScope) {
            it.toDomainModel()
        }

    override val accessToken = storage.accessToken.asStateFlow()

    override val themeMode = storage.themeMode.asStateFlow()

    override val compactMode = storage.compactMode.asStateFlow()

    override suspend fun postSignIn(id: String, password: String): Result<Unit> {
        return try {
            val response = api._postSignIn(PostSignInParams(id, password))
            storage.prefKeyUserId.update(response.userId.toOptional())
            storage.accessToken.update(response.token)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun postSignUp(id: String, password: String, email: String): Result<Unit> {
        return try {
            val response = api._postSignUp(PostSignUpParams(id, password, email))
            storage.prefKeyUserId.update(response.userId.toOptional())
            storage.accessToken.update(response.token)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun fetchUserInfo(): Result<Unit> {
        try {
            val result = api._getUserInfo()
            storage.user.update(result.toOptional())
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun patchUserInfo(nickname: String): Result<Unit> {
        return try {
            val response = api._patchUserInfo(PatchUserInfoParams(nickname))
            storage.user.update(response.toOptional())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun deleteUserAccount(): Result<Unit> {
        try {
            api._deleteUserAccount()
            performLogout()
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun putUserPassword(
        oldPassword: String,
        newPassword: String,
    ): Result<Unit> {
        try {
            val result = api._putUserPassword(
                PutUserPasswordParams(
                    newPassword = newPassword,
                    oldPassword = oldPassword,
                ),
            )
            storage.accessToken.update(result.token)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun postUserPassword(id: String, password: String): Result<Unit> {
        try {
            val result = api._postUserPassword(
                PostUserPasswordParams(
                    id = id,
                    password = password,
                ),
            )
            storage.accessToken.update(result.token)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun postFeedback(email: String, detail: String): Result<Unit> {
        return try {
            api._postFeedback(PostFeedbackParams(email = email, message = detail))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun postForceLogout(): Result<Unit> {
        return try {
            val firebaseToken = getFirebaseToken()
            val userId = storage.prefKeyUserId.get().value
                ?: return Result.Success(Unit)
            api._postForceLogout(
                PostForceLogoutParams(
                    userId = userId,
                    registrationId = firebaseToken,
                ),
            )
            performLogout()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun toggleForceFit(): Result<Unit> {
        try {
            val prevTrimParam = storage.tableTrimParam.get()
            storage.tableTrimParam.update(
                TableTrimParam(
                    dayOfWeekFrom = prevTrimParam.dayOfWeekFrom,
                    dayOfWeekTo = prevTrimParam.dayOfWeekTo,
                    hourFrom = prevTrimParam.hourFrom,
                    hourTo = prevTrimParam.hourTo,
                    forceFitLectures = prevTrimParam.forceFitLectures.not(),
                ).toDataModel(),
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun setDayOfWeekRange(from: Int, to: Int): Result<Unit> {
        try {
            val prevTrimParam = storage.tableTrimParam.get()
            storage.tableTrimParam.update(
                TableTrimParam(
                    dayOfWeekFrom = from,
                    dayOfWeekTo = to,
                    hourFrom = prevTrimParam.hourFrom,
                    hourTo = prevTrimParam.hourTo,
                    forceFitLectures = prevTrimParam.forceFitLectures,
                ).toDataModel(),
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun setHourRange(from: Int, to: Int): Result<Unit> {
        try {
            val prevTrimParam = storage.tableTrimParam.get()
            storage.tableTrimParam.update(
                TableTrimParam(
                    dayOfWeekFrom = prevTrimParam.dayOfWeekFrom,
                    dayOfWeekTo = prevTrimParam.dayOfWeekTo,
                    hourFrom = from,
                    hourTo = to,
                    forceFitLectures = prevTrimParam.forceFitLectures,
                ).toDataModel(),
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun toggleCompactMode(): Result<Unit> {
        try {
            val compactMode = storage.compactMode.get()
            storage.compactMode.update(compactMode.not())
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getAccessToken(): Result<String> {
        return try {
            Result.Success(storage.accessToken.get())
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun performLogout(): Result<Unit> {
        return try {
            LoginManager.getInstance().logOut()
            storage.clearLoginScope()
            CookieManager.getInstance().removeAllCookies(null)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun registerToken(): Result<Unit> {
        return try {
            val token = getFirebaseToken()
            api._registerFirebaseToken(
                token,
                RegisterFirebaseTokenParams(),
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode): Result<Unit> {
        return try {
            storage.themeMode.update(mode)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun findIdByEmail(email: String): Result<Unit> {
        return try {
            api._postFindId(PostFindIdParams(email))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun checkEmailById(id: String): Result<String> {
        return try {
            Result.Success(
                api._postCheckEmailById(PostCheckEmailByIdParams(id)).email,
            )
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun sendPwResetCodeToEmail(email: String): Result<Unit> {
        return try {
            api._postSendPwResetCodeToEmailById(PostSendPwResetCodeParams(email))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun verifyPwResetCode(id: String, code: String): Result<Unit> {
        return try {
            api._postVerifyCodeToResetPassword(PostVerifyPwResetCodeParams(id, code))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun resetPassword(id: String, password: String, code: String): Result<Unit> {
        return try {
            api._postResetPassword(PostResetPasswordParams(id, password, code))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun sendCodeToEmail(email: String): Result<Unit> {
        return try {
            api._postSendCodeToEmail(PostSendCodeToEmailParams(email))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun verifyEmailCode(code: String): Result<Unit> {
        return try {
            api._postVerifyEmailCode(PostVerifyEmailCodeParams(code))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun toggleTitleVisible(): Result<Unit> {
        try {
            val prevTrimParam = storage.tableLectureCustom.get()
            storage.tableLectureCustom.update(
                prevTrimParam.copy(title = prevTrimParam.title.not()),
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun togglePlaceVisible(): Result<Unit> {
        try {
            val prevTrimParam = storage.tableLectureCustom.get()
            storage.tableLectureCustom.update(
                prevTrimParam.copy(place = prevTrimParam.place.not()),
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun toggleLectureNumberVisible(): Result<Unit> {
        try {
            val prevTrimParam = storage.tableLectureCustom.get()
            storage.tableLectureCustom.update(
                prevTrimParam.copy(lectureNumber = prevTrimParam.lectureNumber.not()),
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun toggleInstructorVisible(): Result<Unit> {
        try {
            val prevTrimParam = storage.tableLectureCustom.get()
            storage.tableLectureCustom.update(
                prevTrimParam.copy(instructor = prevTrimParam.instructor.not()),
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getAccessTokenByAuthCode(authCode: String, clientId: String, clientSecret: String): Result<String> {
        return try {
            val accessToken = apiGoogle._getAccessTokenByAuthCode(
                PostAccessTokenByAuthCodeParams(
                    authCode = authCode,
                    clientId = clientId,
                    clientSecret = clientSecret,
                ),
            ).accessToken
            if (accessToken != null) {
                Result.Success(accessToken)
            } else {
                Result.Fail(Unknown("", ""))
            }
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getPushPreferences(): Result<PushPreferences> {
        try {
            val result = api._getPushPreferences().toDomainModel()
            return Result.Success(result)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun postPushPreferences(pushPreferences: PushPreferences): Result<Unit> {
        try {
            api._postPushPreferences(pushPreferences.toNetworkModel())
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getSocialProviders(): Result<GetSocialProvidersResults> {
        return try {
            Result.Success(api._getSocialProviders())
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun postLoginFacebook(facebookToken: String): Result<Unit> {
        return try {
            val response = api._postLoginFacebook(PostSocialLoginParams(facebookToken))
            storage.prefKeyUserId.update(response.userId.toOptional())
            storage.accessToken.update(response.token)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun postLoginGoogle(googleAccessToken: String): Result<Unit> {
        return try {
            val response = api._postLoginGoogle(PostSocialLoginParams(googleAccessToken))
            storage.prefKeyUserId.update(response.userId.toOptional())
            storage.accessToken.update(response.token)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun postLoginKakao(kakaoAccessToken: String): Result<Unit> {
        return try {
            val response = api._postLoginKakao(PostSocialLoginParams(kakaoAccessToken))
            storage.prefKeyUserId.update(response.userId.toOptional())
            storage.accessToken.update(response.token)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun postUserFacebook(facebookToken: String): Result<Unit> {
        return try {
            val response = api._postUserFacebook(PostSocialLoginParams(facebookToken))
            storage.accessToken.update(response.token)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun postUserGoogle(googleAccessToken: String): Result<Unit> {
        return try {
            val response = api._postUserGoogle(PostSocialLoginParams(googleAccessToken))
            storage.accessToken.update(response.token)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun postUserKakao(kakaoAccessToken: String): Result<Unit> {
        return try {
            val response = api._postUserKakao(PostSocialLoginParams(kakaoAccessToken))
            storage.accessToken.update(response.token)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun deleteUserFacebook(): Result<Unit> {
        return try {
            val response = api._deleteUserFacebook()
            storage.accessToken.update(response.token)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun deleteUserGoogle(): Result<Unit> {
        return try {
            val response = api._deleteUserGoogle()
            storage.accessToken.update(response.token)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun deleteUserKakao(): Result<Unit> {
        return try {
            val response = api._deleteUserKakao()
            storage.accessToken.update(response.token)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
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
