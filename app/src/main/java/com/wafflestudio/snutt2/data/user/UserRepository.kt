package com.wafflestudio.snutt2.data.user

import com.wafflestudio.snutt2.domainmodel.SocialProviders
import com.wafflestudio.snutt2.domainmodel.PushPreferences
import com.wafflestudio.snutt2.domainmodel.User
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.ui.ThemeMode
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val user: StateFlow<User?>

    val accessToken: StateFlow<String>

    val themeMode: StateFlow<ThemeMode>

    suspend fun postSignIn(id: String, password: String): Result<Unit>

    suspend fun postSignUp(id: String, password: String, email: String): Result<Unit>

    suspend fun fetchUserInfo(): Result<Unit>

    suspend fun patchUserInfo(nickname: String): Result<Unit>

    suspend fun deleteUserAccount(): Result<Unit>

    suspend fun putUserPassword(oldPassword: String, newPassword: String): Result<Unit>

    suspend fun postUserPassword(id: String, password: String): Result<Unit>

    suspend fun postFeedback(email: String, detail: String): Result<Unit>

    suspend fun postForceLogout(): Result<Unit>

    suspend fun getAccessToken(): Result<String>

    suspend fun performLogout(): Result<Unit>

    suspend fun registerToken(): Result<Unit>

    suspend fun setThemeMode(mode: ThemeMode): Result<Unit>

    suspend fun findIdByEmail(email: String): Result<Unit>

    suspend fun checkEmailById(id: String): Result<String>

    suspend fun sendPwResetCodeToEmail(email: String): Result<Unit>

    suspend fun verifyPwResetCode(id: String, code: String): Result<Unit>

    suspend fun resetPassword(id: String, password: String, code: String): Result<Unit>

    suspend fun sendCodeToEmail(email: String): Result<Unit>

    suspend fun verifyEmailCode(code: String): Result<Unit>

    suspend fun getAccessTokenByAuthCode(
        authCode: String,
        clientId: String,
        clientSecret: String,
    ): Result<String>

    suspend fun getPushPreferences(): Result<PushPreferences>

    suspend fun postPushPreferences(pushPreferences: PushPreferences): Result<Unit>

    suspend fun getSocialProviders(): Result<SocialProviders>

    suspend fun postLoginFacebook(facebookToken: String): Result<Unit>

    suspend fun postLoginGoogle(googleAccessToken: String): Result<Unit>

    suspend fun postLoginKakao(kakaoAccessToken: String): Result<Unit>

    suspend fun postUserFacebook(facebookToken: String): Result<Unit>

    suspend fun postUserGoogle(googleAccessToken: String): Result<Unit>

    suspend fun postUserKakao(kakaoAccessToken: String): Result<Unit>

    suspend fun deleteUserFacebook(): Result<Unit>

    suspend fun deleteUserGoogle(): Result<Unit>

    suspend fun deleteUserKakao(): Result<Unit>
}
