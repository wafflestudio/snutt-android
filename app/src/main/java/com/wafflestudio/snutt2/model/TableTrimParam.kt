package com.wafflestudio.snutt2.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TableTrimParam(
    val dayOfWeekFrom: Int,
    val dayOfWeekTo: Int,
    val hourFrom: Int,
    val hourTo: Int,
    val forceFitLectures: Boolean,
) {
    companion object {
        val Default = TableTrimParam(0, 4, 9, 18, true)
        val SearchOption = TableTrimParam(0, 4, 8, 21, true)
    }
}
