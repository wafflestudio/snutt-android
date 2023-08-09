package com.wafflestudio.snutt2

import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.RemoteConfigDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfig @Inject constructor(
    api: SNUTTRestApi,
    userRepository: UserRepository,
    apiOnError: ApiOnError,
) {
    private val config = callbackFlow {
        userRepository.accessToken.filter { it.isNotEmpty() }.collect {
            withContext(Dispatchers.IO) {
                try {
                    send(api._getRemoteConfig())
                } catch (e: Exception) {
                    apiOnError(e)
                }
            }
        }
        awaitClose {}
    }.stateIn(
        CoroutineScope(Dispatchers.Main),
        SharingStarted.Eagerly,
        RemoteConfigDto()
    )

    val friendBundleSrc: String
        get() = config.value.reactNativeBundleSrc?.src?.get("android") ?: ""

    val vacancyNotificationBannerEnabled: Boolean
        get() = config.value.vacancyBannerConfig.visible

    val sugangSNUUrl: String
        get() = config.value.vacancyUrlConfig.url ?: ""

    val settingPageNewBadgeTitles: List<String>
        get() = config.value.settingsBadgeConfig.new
}
