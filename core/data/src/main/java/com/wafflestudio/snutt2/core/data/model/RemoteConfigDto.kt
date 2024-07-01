package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteConfigDtoT(
    @Json(name = "reactNativeBundleFriends") val reactNativeBundleSrc: com.wafflestudio.snutt2.core.data.model.RemoteConfigDtoT.ReactNativeBundleSrcT? = null,
    @Json(name = "vacancyNotificationBanner") val vacancyBannerConfig: com.wafflestudio.snutt2.core.data.model.RemoteConfigDtoT.VacancyBannerConfigT = com.wafflestudio.snutt2.core.data.model.RemoteConfigDtoT.VacancyBannerConfigT(
        false
    ),
    @Json(name = "vacancySugangSnuUrl") val vacancyUrlConfig: com.wafflestudio.snutt2.core.data.model.RemoteConfigDtoT.VacancyUrlConfigT = com.wafflestudio.snutt2.core.data.model.RemoteConfigDtoT.VacancyUrlConfigT(),
    @Json(name = "settingsBadge") val settingsBadgeConfig: com.wafflestudio.snutt2.core.data.model.RemoteConfigDtoT.SettingsBadgeConfigT = com.wafflestudio.snutt2.core.data.model.RemoteConfigDtoT.SettingsBadgeConfigT(),
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
