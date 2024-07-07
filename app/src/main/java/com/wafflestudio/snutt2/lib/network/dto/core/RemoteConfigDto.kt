package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.RemoteConfigDto as RemoteConfigDtoNetwork

@JsonClass(generateAdapter = true)
data class RemoteConfigDto(
    @Json(name = "reactNativeBundleFriends") val reactNativeBundleSrc: ReactNativeBundleSrc? = null,
    @Json(name = "vacancyNotificationBanner") val vacancyBannerConfig: VacancyBannerConfig = VacancyBannerConfig(false),
    @Json(name = "vacancySugangSnuUrl") val vacancyUrlConfig: VacancyUrlConfig = VacancyUrlConfig(),
    @Json(name = "settingsBadge") val settingsBadgeConfig: SettingsBadgeConfig = SettingsBadgeConfig(),
    @Json(name = "disableMapFeature") val disableMapFeature: Boolean? = null,
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

fun RemoteConfigDtoNetwork.toExternalModel() = RemoteConfigDto(
    reactNativeBundleSrc = this.reactNativeBundleSrc?.toExternalModel(),
    vacancyBannerConfig = this.vacancyBannerConfig.toExternalModel(),
    vacancyUrlConfig = this.vacancyUrlConfig.toExternalModel(),
    settingsBadgeConfig = this.settingsBadgeConfig.toExternalModel(),
    disableMapFeature = this.disableMapFeature,
)

fun RemoteConfigDtoNetwork.ReactNativeBundleSrc.toExternalModel() = RemoteConfigDto.ReactNativeBundleSrc(
    src = this.src,
)

fun RemoteConfigDtoNetwork.SettingsBadgeConfig.toExternalModel() = RemoteConfigDto.SettingsBadgeConfig(
    new = this.new,
)

fun RemoteConfigDtoNetwork.VacancyBannerConfig.toExternalModel() = RemoteConfigDto.VacancyBannerConfig(
    visible = this.visible,
)

fun RemoteConfigDtoNetwork.VacancyUrlConfig.toExternalModel() = RemoteConfigDto.VacancyUrlConfig(
    url = this.url,
)
