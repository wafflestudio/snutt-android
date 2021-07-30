package com.wafflestudio.snutt2.data

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

    val tableTrimParam: Observable<TableTrimParam> = storage.tableTrimParam.asObservable()

    fun setTableTrim(
        dayOfWeekFrom: Int? = null,
        dayOfWeekTo: Int? = null,
        hourFrom: Int? = null,
        hourTo: Int? = null,
        isAuto: Boolean? = null
    ) {
        val current = _tableTrimParam
        _tableTrimParam = _tableTrimParam.copy(
            dayOfWeekFrom ?: current.dayOfWeekFrom,
            dayOfWeekTo ?: current.dayOfWeekTo,
            hourFrom ?: current.hourFrom,
            hourTo ?: current.hourTo,
            isAuto ?: current.forceFitLectures,
        )
    }


}
