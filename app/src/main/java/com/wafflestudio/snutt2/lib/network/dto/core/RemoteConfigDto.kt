package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteConfigDto(
    @Json(name = "reactNativeBundleFriends") val friends: ReactNativeBundleSrc?,
    @Json(name = "reactNativeBundleFriendGst") val friendgst: ReactNativeBundleSrc?,
    @Json(name = "vacancyNotificationBanner") val vacancyBannerOn: Boolean = false,
)
