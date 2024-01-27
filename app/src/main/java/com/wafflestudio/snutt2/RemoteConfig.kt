package com.wafflestudio.snutt2

import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.NetworkConnectivityManager
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.RemoteConfigDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfig @Inject constructor(
    api: SNUTTRestApi,
    userRepository: UserRepository,
    networkConnectivityManager: NetworkConnectivityManager,
) {
    private val config = MutableStateFlow(RemoteConfigDto())
    init {
        CoroutineScope(Dispatchers.Main).launch {
            combine(
                userRepository.accessToken.filter { it.isNotEmpty() },
                networkConnectivityManager.networkConnectivity.filter { it },
            ) { _, _ ->
                withContext(Dispatchers.IO) {
                    runCatching {
                        api._getRemoteConfig()
                    }.onSuccess {
                        config.emit(it)
                    }
                }
            }.collect()
        }
    }

    val friendsBundleSrc: Flow<String>
        get() = config.map { it.reactNativeBundleSrc?.src?.get("android") }.filterNotNull()
    val vacancyNotificationBannerEnabled: Flow<Boolean>
        get() = config.map { it.vacancyBannerConfig.visible }
    val sugangSNUUrl: Flow<String>
        get() = config.map { it.vacancyUrlConfig.url }.filterNotNull()
    val settingPageNewBadgeTitles: Flow<List<String>>
        get() = config.map { it.settingsBadgeConfig.new }
    val embedMapEnabled: Flow<Boolean>
        get() = config.map { it.embedMapConfig.enabled }
}
