package com.wafflestudio.snutt2.model

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.database.model.TableTrimParam as TableTrimParamDatabase

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
        val SearchOption = TableTrimParam(0, 4, 8, 22, true)
        val TimeBlockGridDefault =
            List(SearchOption.dayOfWeekTo - SearchOption.dayOfWeekFrom + 1) {
                List((SearchOption.hourTo - SearchOption.hourFrom + 1) * 2) {
                    false
                }
            }
    }
}

fun TableTrimParamDatabase.toExternalModel() = TableTrimParam(
    dayOfWeekFrom = this.dayOfWeekFrom,
    dayOfWeekTo = this.dayOfWeekTo,
    hourFrom = this.hourFrom,
    hourTo = this.hourTo,
    forceFitLectures = this.forceFitLectures,
)

fun TableTrimParam.toDatabaseModel() = TableTrimParamDatabase(
    dayOfWeekFrom = this.dayOfWeekFrom,
    dayOfWeekTo = this.dayOfWeekTo,
    hourFrom = this.hourFrom,
    hourTo = this.hourTo,
    forceFitLectures = this.forceFitLectures,
)
