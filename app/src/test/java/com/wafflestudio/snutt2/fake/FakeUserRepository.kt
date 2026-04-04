package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.PushPreferences
import com.wafflestudio.snutt2.domainmodel.User
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.dto.GetSocialProvidersResults
import com.wafflestudio.snutt2.ui.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeUserRepository : UserRepository {

    private val _user = MutableStateFlow<User?>(null)
    override val user: StateFlow<User?> = _user.asStateFlow()

    private val _accessToken = MutableStateFlow("")
    override val accessToken: StateFlow<String> = _accessToken.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.AUTO)
    override val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    // region 테스트 제어용 필드

    var findIdByEmailResult: Result<Unit> = Result.Success(Unit)
    var findIdByEmailCalledWith: String? = null
        private set

    // endregion

    override suspend fun findIdByEmail(email: String): Result<Unit> {
        findIdByEmailCalledWith = email
        return findIdByEmailResult
    }

    // region 미사용 메서드

    override suspend fun postSignIn(id: String, password: String): Result<Unit> = TODO()
    override suspend fun postSignUp(id: String, password: String, email: String): Result<Unit> = TODO()
    override suspend fun fetchUserInfo(): Result<Unit> = TODO()
    override suspend fun patchUserInfo(nickname: String): Result<Unit> = TODO()
    override suspend fun deleteUserAccount(): Result<Unit> = TODO()
    override suspend fun putUserPassword(oldPassword: String, newPassword: String): Result<Unit> = TODO()
    override suspend fun postUserPassword(id: String, password: String): Result<Unit> = TODO()
    override suspend fun postFeedback(email: String, detail: String): Result<Unit> = TODO()
    override suspend fun postForceLogout(): Result<Unit> = TODO()
    override suspend fun getAccessToken(): Result<String> = TODO()
    override suspend fun performLogout(): Result<Unit> = TODO()
    override suspend fun registerToken(): Result<Unit> = TODO()
    override suspend fun setThemeMode(mode: ThemeMode): Result<Unit> = TODO()
    override suspend fun checkEmailById(id: String): Result<String> = TODO()
    override suspend fun sendPwResetCodeToEmail(email: String): Result<Unit> = TODO()
    override suspend fun verifyPwResetCode(id: String, code: String): Result<Unit> = TODO()
    override suspend fun resetPassword(id: String, password: String, code: String): Result<Unit> = TODO()
    override suspend fun sendCodeToEmail(email: String): Result<Unit> = TODO()
    override suspend fun verifyEmailCode(code: String): Result<Unit> = TODO()
    override suspend fun getAccessTokenByAuthCode(authCode: String, clientId: String, clientSecret: String): Result<String> = TODO()
    override suspend fun getPushPreferences(): Result<PushPreferences> = TODO()
    override suspend fun postPushPreferences(pushPreferences: PushPreferences): Result<Unit> = TODO()
    override suspend fun getSocialProviders(): Result<GetSocialProvidersResults> = TODO()
    override suspend fun postLoginFacebook(facebookToken: String): Result<Unit> = TODO()
    override suspend fun postLoginGoogle(googleAccessToken: String): Result<Unit> = TODO()
    override suspend fun postLoginKakao(kakaoAccessToken: String): Result<Unit> = TODO()
    override suspend fun postUserFacebook(facebookToken: String): Result<Unit> = TODO()
    override suspend fun postUserGoogle(googleAccessToken: String): Result<Unit> = TODO()
    override suspend fun postUserKakao(kakaoAccessToken: String): Result<Unit> = TODO()
    override suspend fun deleteUserFacebook(): Result<Unit> = TODO()
    override suspend fun deleteUserGoogle(): Result<Unit> = TODO()
    override suspend fun deleteUserKakao(): Result<Unit> = TODO()

    // endregion
}
