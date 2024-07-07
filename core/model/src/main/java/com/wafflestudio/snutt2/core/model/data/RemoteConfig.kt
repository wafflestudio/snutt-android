package com.wafflestudio.snutt2.core.model.data

data class RemoteConfig(
    val friendsBundleSrc: String?,
    val vacancyNotificationBannerEnabled: Boolean,
    val sugangSNUUrl: String?,
    val settingPageNewBadgeTitles: List<String>,
    val disableMapFeature: Boolean
)