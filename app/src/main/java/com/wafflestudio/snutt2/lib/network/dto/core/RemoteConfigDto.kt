package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteConfigDto(
    @Json(name = "reactNativeBundleFriends") val reactNativeBundleSrc: ReactNativeBundleSrc? = null,
    @Json(name = "vacancyNotificationBanner") val vacancyBannerConfig: VacancyBannerConfig = VacancyBannerConfig(false),
    @Json(name = "vacancySugangSnuUrl") val vacancyUrlConfig: VacancyUrlConfig = VacancyUrlConfig(),
    @Json(name = "settingsBadge") val settingsBadgeConfig: SettingsBadgeConfig = SettingsBadgeConfig(),
    @Json(name = "disableMapFeature") val disableMapFeature: Boolean = false,
) {
    data class ReactNativeBundleSrc(
        @Json(name = "src") val src: Map<String, String>,
    )

    data class SettingsBadgeConfig(
        @Json(name = "new") val new: List<String> = emptyList(),
    )

    data class VacancyBannerConfig(
        @Json(name = "visible") val visible: Boolean = false,
    )

    data class VacancyUrlConfig(
        @Json(name = "url") val url: String? = null,
    )
}
