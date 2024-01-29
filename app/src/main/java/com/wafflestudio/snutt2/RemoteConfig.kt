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
                    }.onFailure {
                        // NOTE: 서버 장애나 네트워크 오프라인 등의 이유로 true 인지 false 인지 모를 경우 지도를 숨긴다.
                        // https://wafflestudio.slack.com/archives/C0PAVPS5T/p1706504661308259?thread_ts=1706451688.745159&cid=C0PAVPS5T
                        config.emit(RemoteConfigDto(disableMapFeature = true))
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
    val disableMapFeature: Flow<Boolean>
        get() = config.map { it.disableMapFeature }
}
