package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.dto.GetUserFacebookResults
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialLinkViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    val userInfo: StateFlow<UserDto?> = userRepository.user

    suspend fun fetchUserInfo() {
        userRepository.fetchUserInfo()
    }

    suspend fun fetchUserFacebook(): GetUserFacebookResults {
        return userRepository.getUserFacebook()
    }

    suspend fun connectFacebook(token: String) {
        userRepository.postUserFacebook(token)
    }

    suspend fun disconnectFacebook() {
        userRepository.deleteUserFacebook()
    }
}
