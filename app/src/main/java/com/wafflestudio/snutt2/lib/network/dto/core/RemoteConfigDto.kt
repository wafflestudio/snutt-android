package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.config.ReactNativeBundleSrc
import com.wafflestudio.snutt2.lib.network.dto.config.SettingsBadgeConfig
import com.wafflestudio.snutt2.lib.network.dto.config.VacancyBannerConfig
import com.wafflestudio.snutt2.lib.network.dto.config.VacancyUrlConfig

@JsonClass(generateAdapter = true)
data class RemoteConfigDto(
    @Json(name = "reactNativeBundleFriends") val reactNativeBundleSrc: ReactNativeBundleSrc? = null,
    @Json(name = "vacancyNotificationBanner") val vacancyBannerConfig: VacancyBannerConfig = VacancyBannerConfig(false),
    @Json(name = "vacancySugangSnuUrl") val vacancyUrlConfig: VacancyUrlConfig = VacancyUrlConfig(),
    @Json(name = "settingsBadge") val settingsBadgeConfig: SettingsBadgeConfig = SettingsBadgeConfig()
)
