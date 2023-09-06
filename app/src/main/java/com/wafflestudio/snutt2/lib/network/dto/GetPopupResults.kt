package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetPopupResults(
    @Json(name = "content") val popups: List<Popup>,
) {
    data class Popup(
        @Json(name = "key") val key: String,
        @Json(name = "image_url") val url: String,
        @Json(name = "hidden_days") val popupHideDays: Int?,
    )
}
