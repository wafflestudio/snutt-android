package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteConfigDto(
    @param:Json(name = "vacancyNotificationBanner") val vacancyBannerConfig: VacancyBannerConfig = VacancyBannerConfig(
        false,
    ),
    @param:Json(name = "vacancySugangSnuUrl") val vacancyUrlConfig: VacancyUrlConfig = VacancyUrlConfig(),
    @param:Json(name = "settingsBadge") val settingsBadgeConfig: SettingsBadgeConfig = SettingsBadgeConfig(),
    @param:Json(name = "disableMapFeature") val disableMapFeature: Boolean? = null,
    @param:Json(name = "notice") val noticeConfig: NoticeConfig? = null,
) {
    @JsonClass(generateAdapter = true)
    data class SettingsBadgeConfig(
        @param:Json(name = "new") val new: List<String> = emptyList(),
    )

    @JsonClass(generateAdapter = true)
    data class VacancyBannerConfig(
        @param:Json(name = "visible") val visible: Boolean = false,
    )

    @JsonClass(generateAdapter = true)
    data class VacancyUrlConfig(
        @param:Json(name = "url") val url: String? = null,
    )

    @JsonClass(generateAdapter = true)
    data class NoticeConfig(
        @param:Json(name = "visible") val visible: Boolean = false,
        @param:Json(name = "title") val title: String? = null,
        @param:Json(name = "content") val content: String? = null,
    )
}
