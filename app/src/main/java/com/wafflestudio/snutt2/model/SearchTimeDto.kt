package com.wafflestudio.snutt2.model

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.SearchTimeDto as SearchTimeDtoNetwork

@JsonClass(generateAdapter = true)
data class SearchTimeDto(
    val day: Int,
    val startMinute: Int,
    val endMinute: Int,
) {
    companion object {
        const val FIRST = 0
        const val MIDDAY = 720
        const val LAST = 1435
    }
}

fun SearchTimeDto.toNetworkModel() = SearchTimeDtoNetwork(
    day = this.day,
    startMinute = this.startMinute,
    endMinute = this.endMinute,
)