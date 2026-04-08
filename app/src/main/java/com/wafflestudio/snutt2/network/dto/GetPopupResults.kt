package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetPopupResults(
    @param:Json(name = "content") val popups: List<Popup>,
) {
    data class Popup(
        @param:Json(name = "key") val key: String,
        @param:Json(name = "imageUri") val imageUri: String,
        @param:Json(name = "linkUrl") val linkUrl: String?,
        @param:Json(name = "hiddenDays") val popupHideDays: Int?,
    )
}
