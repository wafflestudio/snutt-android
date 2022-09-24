package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.dto.GetUserFacebookResults
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.model.TableTrimParam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val trimParam = userRepository.tableTrimParam.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), TableTrimParam.Default
    )

    val userInfo = userRepository.user.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), UserDto()
    )

    val accessToken = userRepository.accessToken().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), null
    )

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
        userRepository.deleteFirebaseToken()
        // onSuccess, onError 분기 (settingsPage에서?)
        // TODO
    }
}
