package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.GetTableListResults
import com.wafflestudio.snutt2.lib.network.dto.PostCopyTableResults
import com.wafflestudio.snutt2.lib.network.dto.PostTableParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableParams
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.toOptional
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableRepository @Inject constructor(
    private val snuttRestApi: SNUTTRestApi,
    private val storage: SNUTTStorage
) {
    private var _tableMap: Map<String, TableDto>
        get() = storage.tableMap.getValue()
        set(value) {
            storage.tableMap.setValue(value)
        }
    val tableMap = storage.tableMap.asObservable()

    val currentTable = storage.lastViewedTable.asObservable()

    fun getCurrentTable(): TableDto? {
        return storage.lastViewedTable.getValue().get()
    }

    fun refreshTable(tableId: String): Single<TableDto> {
        return snuttRestApi.getTableById(tableId)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                _tableMap = _tableMap.toMutableMap().apply { put(it.id, it) }
                storage.lastViewedTable.setValue(it.toOptional())
            }
    }

    fun fetchDefaultTable(): Single<TableDto> {
        return storage.lastViewedTable.getValue().let { table ->
            if (table.isEmpty()) {
                snuttRestApi.getRecentTable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doOnSuccess {
                        _tableMap = _tableMap.toMutableMap().apply { put(it.id, it) }
                        storage.lastViewedTable.setValue(it.toOptional())
                    }
            } else {
                Single.just(table.get())
            }
        }
    }

    fun fetchTableList(): Single<GetTableListResults> {
        return snuttRestApi.getTableList()
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                _tableMap = result.map { it.id to it }.toMap()
            }
    }

    fun createTable(year: Long, semester: Long, title: String?): Single<List<TableDto>> {
        return snuttRestApi.postTable(PostTableParams(year, semester, title))
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                _tableMap = result.map { it.id to it }.toMap()
            }
    }

    fun deleteTable(id: String): Single<List<TableDto>> {
        return snuttRestApi.deleteTable(id)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                _tableMap = result.map { it.id to it }.toMap()
            }
    }

    fun putTable(id: String, title: String): Single<List<TableDto>> {
        return snuttRestApi.putTable(id, PutTableParams(title))
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                storage.lastViewedTable.getValue().get()?.let {
                    storage.lastViewedTable.setValue(it.copy(title = title).toOptional())
                }
                _tableMap = result.map { it.id to it }.toMap()
            }
    }

    fun copyTable(id: String): Single<PostCopyTableResults> {
        return snuttRestApi.copyTable(id)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                _tableMap = result.map { it.id to it }.toMap()
            }
    }
}
