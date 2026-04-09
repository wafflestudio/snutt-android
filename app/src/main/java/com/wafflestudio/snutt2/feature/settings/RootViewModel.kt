package com.wafflestudio.snutt2.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val accessToken: StateFlow<String> = userRepository.accessToken

    val themeMode: StateFlow<ThemeMode> = userRepository.themeMode

    fun registerPushToken() {
        viewModelScope.launch {
            userRepository.registerToken()
        }
    }
}
