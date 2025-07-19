package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.ui.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val trimParam: StateFlow<TableTrimParam> = userRepository.tableTrimParam

    val tableLectureCustomOption: StateFlow<TableLectureCustom> = userRepository.tableLectureCustomOption

    val userInfo: StateFlow<UserDto?> = userRepository.user

    val accessToken: StateFlow<String> = userRepository.accessToken

    val themeMode: StateFlow<ThemeMode> = userRepository.themeMode

    val compactMode: StateFlow<Boolean> = userRepository.compactMode

    val firstBookmarkAlert: StateFlow<Boolean> = userRepository.firstBookmarkAlert

    suspend fun fetchUserInfo() {
        userRepository.fetchUserInfo()
    }

    suspend fun loginLocal(id: String, password: String) {
        userRepository.postSignIn(id, password)
    }

    suspend fun loginFacebook(facebookToken: String) {
        userRepository.postLoginFacebook(facebookToken)
    }

    suspend fun loginGoogle(googleAccessToken: String) {
        userRepository.postLoginGoogle(googleAccessToken)
    }

    suspend fun loginKakao(kakaoAccessToken: String) {
        userRepository.postLoginKakao(kakaoAccessToken)
    }

    suspend fun changeNickname(nickname: String) {
        userRepository.patchUserInfo(nickname)
    }

    suspend fun sendFeedback(email: String, detail: String) {
        return userRepository.postFeedback(email, detail)
    }

    suspend fun registerPushToken() {
        userRepository.registerToken()
    }

    suspend fun signUpLocal(idField: String, emailField: String, passwordField: String) {
        userRepository.postSignUp(id = idField, password = passwordField, email = emailField)
    }

    suspend fun fetchPopup() {
        userRepository.fetchAndSetPopup()
    }

    suspend fun closePopupWithHiddenDays() {
        userRepository.closePopupWithHiddenDays()
    }

    suspend fun closePopup() {
        userRepository.closePopup()
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        userRepository.setThemeMode(mode)
    }

    suspend fun findIdByEmail(email: String) {
        userRepository.findIdByEmail(email)
    }

    suspend fun sendCodeToEmail(email: String) {
        userRepository.sendCodeToEmail(email)
    }

    suspend fun verifyEmailCode(code: String) {
        userRepository.verifyEmailCode(code)
    }

    suspend fun setFirstBookmarkAlertShown() {
        userRepository.setFirstBookmarkAlertShown()
    }

    suspend fun getAccessTokenByAuthCode(authCode: String, clientId: String, clientSecret: String): String? {
        return userRepository.getAccessTokenByAuthCode(authCode = authCode, clientId = clientId, clientSecret = clientSecret)
    }
}
