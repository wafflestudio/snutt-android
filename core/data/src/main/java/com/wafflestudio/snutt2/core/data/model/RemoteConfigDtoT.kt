package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteConfigDtoT(
    @Json(name = "reactNativeBundleFriends") val reactNativeBundleSrc: ReactNativeBundleSrcT? = null,
    @Json(name = "vacancyNotificationBanner") val vacancyBannerConfig: VacancyBannerConfigT = VacancyBannerConfigT(
        false
    ),
    @Json(name = "vacancySugangSnuUrl") val vacancyUrlConfig: VacancyUrlConfigT = VacancyUrlConfigT(),
    @Json(name = "settingsBadge") val settingsBadgeConfig: SettingsBadgeConfigT = SettingsBadgeConfigT(),
    @Json(name = "disableMapFeature") val disableMapFeature: Boolean? = null,
) {
    data class ReactNativeBundleSrcT(
        @Json(name = "src") val src: Map<String, String>,
    )

    data class SettingsBadgeConfigT(
        @Json(name = "new") val new: List<String> = emptyList(),
    )

    data class VacancyBannerConfigT(
        @Json(name = "visible") val visible: Boolean = false,
    )

    data class VacancyUrlConfigT(
        @Json(name = "url") val url: String? = null,
    )
}
