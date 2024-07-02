package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.GetPopupResults as GetPopupResultsNetwork

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

fun GetPopupResultsNetwork.toExternalModel() = GetPopupResults(
    popups = this.popups.map { it.toExternalModel() },
)

fun GetPopupResultsNetwork.Popup.toExternalModel() = GetPopupResults.Popup(
    key = this.key,
    uri = this.uri,
    popupHideDays = popupHideDays,
)