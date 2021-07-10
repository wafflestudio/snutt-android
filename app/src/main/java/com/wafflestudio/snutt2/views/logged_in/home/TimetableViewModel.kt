package com.wafflestudio.snutt2.views.logged_in.home

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.data.TableRepository
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.model.TableTrimParam
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val tableRepository: TableRepository,
    private val storage: SNUTTStorage,
    private val apiOnError: ApiOnError
) : ViewModel() {

    val currentTimetable: Observable<TableDto>
        get() = storage.lastViewedTable
            .asObservable()
            .filterEmpty()

    val trimParam: Observable<TableTrimParam>
        get() = storage.tableTrimParam
            .asObservable()

    fun loadTable() {
        storage.lastViewedTable.getValue().get()?.id.let { tableId ->
            if (tableId == null) {
                tableRepository.getDefaultTable()
            } else {
                tableRepository.refreshTable(tableId)
            }
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = apiOnError)
    }

    fun setTable(tableId: String) {
        tableRepository.refreshTable(tableId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = apiOnError)
    }
}
