package com.wafflestudio.snutt2

import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSource
import com.wafflestudio.snutt2.core.qualifiers.App
import com.wafflestudio.snutt2.core.qualifiers.CoreNetwork
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.NetworkConnectivityManager
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.RemoteConfigDto
import com.wafflestudio.snutt2.lib.network.dto.core.toExternalModel
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
    @CoreNetwork api: SNUTTNetworkDataSource,
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
                        config.emit(it.toExternalModel())
                    }.onFailure {
                        // NOTE: 서버 장애나 네트워크 오프라인 등의 이유로 config를 받아오지 못한 경우 지도를 숨긴다.
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
        // NOTE: 평상시에는 필드가 null로 내려오고, 이는 로직상 false 취급이다. 지도를 급히 비활성화해야 할 경우 true가 내려온다.
        // https://wafflestudio.slack.com/archives/C0PAVPS5T/p1706542084934709?thread_ts=1706451688.745159&cid=C0PAVPS5T
        get() = config.map { it.disableMapFeature ?: false }
}
