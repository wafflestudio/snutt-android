package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.dto.GetUserFacebookResults
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.model.TableTrimParam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val trimParam: StateFlow<TableTrimParam> = userRepository.tableTrimParam

    val userInfo: StateFlow<UserDto?> = userRepository.user

    val accessToken: StateFlow<String> = userRepository.accessToken

    suspend fun fetchUserInfo() {
        userRepository.fetchUserInfo()
    }

    suspend fun setHourRange(from: Int, to: Int) {
        userRepository.setTableTrim(hourFrom = from, hourTo = to)
    }

    suspend fun setDayOfWeekRange(from: Int, to: Int) {
        userRepository.setTableTrim(dayOfWeekFrom = from, dayOfWeekTo = to)
    }

    suspend fun setAutoTrim(enable: Boolean) {
        userRepository.setTableTrim(isAuto = enable)
    }

    suspend fun loginLocal(id: String, password: String) {
        userRepository.postSignIn(id, password)
    }

    suspend fun loginFacebook(facebookId: String, facebookToken: String) {
        userRepository.postLoginFacebook(facebookId, facebookToken)
    }

    suspend fun addNewLocalId(id: String, password: String) {
        userRepository.postUserPassword(id, password)
    }

    suspend fun changePassword(oldPassword: String, newPassword: String) {
        userRepository.putUserPassword(oldPassword, newPassword)
    }

    suspend fun changeEmail(email: String) {
        userRepository.putUserInfo(email)
    }

    suspend fun fetchUserFacebook(): GetUserFacebookResults {
        return userRepository.getUserFacebook()
    }

    suspend fun connectFacebook(id: String, token: String) {
        userRepository.postUserFacebook(id, token)
    }

    suspend fun disconnectFacebook() {
        userRepository.deleteUserFacebook()
    }

    suspend fun leave() {
        userRepository.deleteUserAccount()
    }

    suspend fun getAccessToken(): String {
        return userRepository.getAccessToken()
    }

    suspend fun sendFeedback(email: String, detail: String) {
        return userRepository.postFeedback(email, detail)
    }

    suspend fun performLogout() {
        userRepository.performLogout()
    }

    suspend fun registerPushToken() {
        userRepository.registerToken()
    }

    suspend fun signUpLocal(idField: String, emailField: String, passwordField: String) {
        userRepository.postSignUp(id = idField, password = passwordField, email = emailField)
    }

    suspend fun signUpFacebook(id: String, token: String) {
        userRepository.postLoginFacebook(
            facebookId = id,
            facebookToken = token
        )
    }
}
