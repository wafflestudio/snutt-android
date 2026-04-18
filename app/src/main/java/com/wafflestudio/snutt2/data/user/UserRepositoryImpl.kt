package com.wafflestudio.snutt2.data.user

import android.webkit.CookieManager
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.data.mapper.toDomain
import com.wafflestudio.snutt2.data.mapper.toDto
import com.wafflestudio.snutt2.domain.Unknown
import com.wafflestudio.snutt2.domain.model.PushPreferences
import com.wafflestudio.snutt2.domain.model.SocialProviders
import com.wafflestudio.snutt2.domain.model.User
import com.wafflestudio.snutt2.lib.map
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.api.google.PostAccessTokenByAuthCodeParams
import com.wafflestudio.snutt2.network.api.google.SNUTTRestApiForGoogle
import com.wafflestudio.snutt2.network.dto.PatchUserInfoParams
import com.wafflestudio.snutt2.network.dto.PostCheckEmailByIdParams
import com.wafflestudio.snutt2.network.dto.PostFeedbackParams
import com.wafflestudio.snutt2.network.dto.PostFindIdParams
import com.wafflestudio.snutt2.network.dto.PostForceLogoutParams
import com.wafflestudio.snutt2.network.dto.PostResetPasswordParams
import com.wafflestudio.snutt2.network.dto.PostSendCodeToEmailParams
import com.wafflestudio.snutt2.network.dto.PostSendPwResetCodeParams
import com.wafflestudio.snutt2.network.dto.PostSignInParams
import com.wafflestudio.snutt2.network.dto.PostSignUpParams
import com.wafflestudio.snutt2.network.dto.PostSocialLoginParams
import com.wafflestudio.snutt2.network.dto.PostUserPasswordParams
import com.wafflestudio.snutt2.network.dto.PostVerifyEmailCodeParams
import com.wafflestudio.snutt2.network.dto.PostVerifyPwResetCodeParams
import com.wafflestudio.snutt2.network.dto.PutUserPasswordParams
import com.wafflestudio.snutt2.network.dto.RegisterFirebaseTokenParams
import com.wafflestudio.snutt2.network.error.toDomainError
import com.wafflestudio.snutt2.storage.SNUTTStorage
import com.wafflestudio.snutt2.storage.toOptional
import com.wafflestudio.snutt2.storage.unwrap
import com.wafflestudio.snutt2.ui.theme.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
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
        .map(externalScope) { it?.toDomain() }

    override val accessToken = storage.accessToken.asStateFlow()

    override val themeMode = storage.themeMode.asStateFlow()

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
            val result = api._getPushPreferences().toDomain()
            return Result.Success(result)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun postPushPreferences(pushPreferences: PushPreferences): Result<Unit> {
        try {
            api._postPushPreferences(pushPreferences.toDto())
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getSocialProviders(): Result<SocialProviders> {
        return try {
            val response = api._getSocialProviders()
            Result.Success(
                SocialProviders(
                    local = response.local,
                    facebook = response.facebook,
                    google = response.google,
                    kakao = response.kakao,
                    apple = response.apple,
                ),
            )
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
}
