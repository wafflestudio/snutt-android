package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.RemoteConfig
import com.wafflestudio.snutt2.core.network.model.RemoteConfigDto

// TODO : 논의 중, RemoteConfig라는 model 자체가 필요 없다는 얘기도 있었다.
fun RemoteConfigDto.toExternalModel() = RemoteConfig(
    friendsBundleSrc = reactNativeBundleSrc?.src?.get("android"),
    vacancyNotificationBannerEnabled = vacancyBannerConfig.visible,
    sugangSNUUrl = vacancyUrlConfig.url,
    settingPageNewBadgeTitles = settingsBadgeConfig.new,
    disableMapFeature = disableMapFeature ?: false,
)