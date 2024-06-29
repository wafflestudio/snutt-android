package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetPopupResults(
    @Json(name = "content") val popups: List<Popup>,
) {
    data class Popup(
        @Json(name = "key") val key: String,
        @Json(name = "imageUri") val uri: String,
        @Json(name = "hiddenDays") val popupHideDays: Int?,
    )
}
