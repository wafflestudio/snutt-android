package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.lib.data.DataValue
import com.wafflestudio.snutt2.lib.preferences.context.PrefValue
import com.wafflestudio.snutt2.model.TableTrimParam
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val storage: SNUTTStorage
) {
    private var _tableTrimParam: TableTrimParam
        get() = storage.tableTrimParam.get()
        set(value) {
            storage.tableTrimParam.update(value)
        }

    val tableTrimParam: DataValue<TableTrimParam> = storage.tableTrimParam

    fun setTableTrim(
        dayOfWeekFrom: Int? = null,
        dayOfWeekTo: Int? = null,
        hourFrom: Int? = null,
        hourTo: Int? = null,
        isAuto: Boolean? = null
    ) {
        val current = _tableTrimParam
        _tableTrimParam = _tableTrimParam.copy(
            dayOfWeekFrom = dayOfWeekFrom ?: current.dayOfWeekFrom,
            dayOfWeekTo = dayOfWeekTo ?: current.dayOfWeekTo,
            hourFrom = hourFrom ?: current.hourFrom,
            hourTo = hourTo ?: current.hourTo,
            forceFitLectures = isAuto ?: current.forceFitLectures,
        )
    }


}
