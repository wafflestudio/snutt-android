package com.wafflestudio.snutt2.views.logged_in.thememarket

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThemeMarketViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val accessToken get() = userRepository.accessToken
}
