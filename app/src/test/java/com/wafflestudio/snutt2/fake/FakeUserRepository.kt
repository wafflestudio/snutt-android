package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.model.SocialProviders
import com.wafflestudio.snutt2.domain.model.PushPreferences
import com.wafflestudio.snutt2.domain.model.User
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.ui.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserRepository : UserRepository {

    override val user = MutableStateFlow<User?>(null)
    override val accessToken = MutableStateFlow("")
    override val themeMode = MutableStateFlow(ThemeMode.AUTO)

    // region 테스트 제어용 필드

    var findIdByEmailResult: Result<Unit> = Result.Success(Unit)
    var findIdByEmailCalledWith: String? = null
        private set

    var setThemeModeResult: Result<Unit> = Result.Success(Unit)
    var setThemeModeCalledWith: ThemeMode? = null
        private set

    var postFeedbackResult: Result<Unit> = Result.Success(Unit)
    var postFeedbackCalledWith: Pair<String, String>? = null
        private set

    var checkEmailByIdResult: Result<String> = Result.Success("")
    var checkEmailByIdCalledWith: String? = null
        private set

    var sendPwResetCodeToEmailResult: Result<Unit> = Result.Success(Unit)
    var sendPwResetCodeToEmailCalledWith: String? = null
        private set

    var verifyPwResetCodeResult: Result<Unit> = Result.Success(Unit)
    var verifyPwResetCodeCalledWith: Pair<String, String>? = null
        private set

    var resetPasswordResult: Result<Unit> = Result.Success(Unit)
    var resetPasswordCalledWith: Triple<String, String, String>? = null
        private set

    var putUserPasswordResult: Result<Unit> = Result.Success(Unit)
    var putUserPasswordCalledWith: Pair<String, String>? = null
        private set

    var postUserPasswordResult: Result<Unit> = Result.Success(Unit)
    var postUserPasswordCalledWith: Pair<String, String>? = null
        private set

    var deleteUserAccountResult: Result<Unit> = Result.Success(Unit)
    var deleteUserAccountCalled = false
        private set

    var fetchUserInfoResult: Result<Unit> = Result.Success(Unit)
    var fetchUserInfoCalled = false
        private set

    var postForceLogoutResult: Result<Unit> = Result.Success(Unit)
    var postForceLogoutCalled = false
        private set

    var performLogoutResult: Result<Unit> = Result.Success(Unit)
    var performLogoutCalled = false
        private set

    var getPushPreferencesResult: Result<PushPreferences> = Result.Success(
        PushPreferences(lectureUpdate = false, vacancyNotification = false, lectureDiary = false),
    )

    var postPushPreferencesResult: Result<Unit> = Result.Success(Unit)
    var postPushPreferencesCalledWith: PushPreferences? = null
        private set

    // endregion

    override suspend fun findIdByEmail(email: String): Result<Unit> {
        findIdByEmailCalledWith = email
        return findIdByEmailResult
    }

    // region 미사용 메서드

    override suspend fun postSignIn(id: String, password: String): Result<Unit> = TODO()
    override suspend fun postSignUp(id: String, password: String, email: String): Result<Unit> = TODO()
    override suspend fun fetchUserInfo(): Result<Unit> {
        fetchUserInfoCalled = true
        return fetchUserInfoResult
    }
    override suspend fun patchUserInfo(nickname: String): Result<Unit> = TODO()
    override suspend fun deleteUserAccount(): Result<Unit> {
        deleteUserAccountCalled = true
        return deleteUserAccountResult
    }
    override suspend fun putUserPassword(oldPassword: String, newPassword: String): Result<Unit> {
        putUserPasswordCalledWith = oldPassword to newPassword
        return putUserPasswordResult
    }
    override suspend fun postUserPassword(id: String, password: String): Result<Unit> {
        postUserPasswordCalledWith = id to password
        return postUserPasswordResult
    }
    override suspend fun postFeedback(email: String, detail: String): Result<Unit> {
        postFeedbackCalledWith = email to detail
        return postFeedbackResult
    }
    override suspend fun postForceLogout(): Result<Unit> {
        postForceLogoutCalled = true
        return postForceLogoutResult
    }
    override suspend fun getAccessToken(): Result<String> = TODO()
    override suspend fun performLogout(): Result<Unit> {
        performLogoutCalled = true
        return performLogoutResult
    }
    override suspend fun registerToken(): Result<Unit> = TODO()
    override suspend fun setThemeMode(mode: ThemeMode): Result<Unit> {
        setThemeModeCalledWith = mode
        return setThemeModeResult
    }
    override suspend fun checkEmailById(id: String): Result<String> {
        checkEmailByIdCalledWith = id
        return checkEmailByIdResult
    }
    override suspend fun sendPwResetCodeToEmail(email: String): Result<Unit> {
        sendPwResetCodeToEmailCalledWith = email
        return sendPwResetCodeToEmailResult
    }
    override suspend fun verifyPwResetCode(id: String, code: String): Result<Unit> {
        verifyPwResetCodeCalledWith = id to code
        return verifyPwResetCodeResult
    }
    override suspend fun resetPassword(id: String, password: String, code: String): Result<Unit> {
        resetPasswordCalledWith = Triple(id, password, code)
        return resetPasswordResult
    }
    override suspend fun sendCodeToEmail(email: String): Result<Unit> = TODO()
    override suspend fun verifyEmailCode(code: String): Result<Unit> = TODO()
    override suspend fun getAccessTokenByAuthCode(authCode: String, clientId: String, clientSecret: String): Result<String> = TODO()
    override suspend fun getPushPreferences(): Result<PushPreferences> {
        return getPushPreferencesResult
    }
    override suspend fun postPushPreferences(pushPreferences: PushPreferences): Result<Unit> {
        postPushPreferencesCalledWith = pushPreferences
        return postPushPreferencesResult
    }
    override suspend fun getSocialProviders(): Result<SocialProviders> = TODO()
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
