package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.TableTrimParam

@JsonClass(generateAdapter = true)
data class TableTrimParamData(
    val dayOfWeekFrom: Int,
    val dayOfWeekTo: Int,
    val hourFrom: Int,
    val hourTo: Int,
    val forceFitLectures: Boolean,
) {
    companion object {
        val Default = TableTrimParamData(0, 4, 9, 18, true)
    }
}

fun TableTrimParamData.toDomainModel(): TableTrimParam = TableTrimParam(
    dayOfWeekFrom = dayOfWeekFrom,
    dayOfWeekTo = dayOfWeekTo,
    hourFrom = hourFrom,
    hourTo = hourTo,
    forceFitLectures = forceFitLectures,
)
