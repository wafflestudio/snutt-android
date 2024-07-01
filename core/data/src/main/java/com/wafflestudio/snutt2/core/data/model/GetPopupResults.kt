package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetPopupResultsT(
    @Json(name = "content") val popups: List<com.wafflestudio.snutt2.core.data.model.GetPopupResultsT.PopupT>,
) {
    data class PopupT(
        @Json(name = "key") val key: String,
        @Json(name = "imageUri") val uri: String,
        @Json(name = "hiddenDays") val popupHideDays: Int?,
    )
}
