package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.lib.data.DataValue
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.*
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.toOptional
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableRepository @Inject constructor(
    private val snuttRestApi: SNUTTRestApi,
    private val storage: SNUTTStorage
) {
    val tableMap: DataValue<Map<String, SimpleTableDto>> = storage.tableMap

    fun refreshTable(tableId: String): Single<TableDto> {
        return snuttRestApi.getTableById(tableId)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                storage.lastViewedTable.update(it.toOptional())
            }
    }

    fun fetchDefaultTable(): Single<TableDto> {
        val table = storage.lastViewedTable.get().value
        return if (table == null) {
            snuttRestApi.getRecentTable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess {
                    storage.lastViewedTable.update(it.toOptional())
                }
        } else {
            Single.just(table)
        }
    }

    fun fetchTableList(): Single<GetTableListResults> {
        return snuttRestApi.getTableList()
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                tableMap.update(result.map { it.id to it }.toMap())
            }
    }

    fun createTable(year: Long, semester: Long, title: String?): Single<List<SimpleTableDto>> {
        return snuttRestApi.postTable(PostTableParams(year, semester, title))
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                tableMap.update(result.map { it.id to it }.toMap())
            }
    }

    fun deleteTable(id: String): Single<List<SimpleTableDto>> {
        return snuttRestApi.deleteTable(id)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                tableMap.update(result.map { it.id to it }.toMap())
            }
    }

    fun updateTableName(id: String, title: String): Single<List<SimpleTableDto>> {
        return snuttRestApi.putTable(id, PutTableParams(title))
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                storage.lastViewedTable.get().get()?.let {
                    storage.lastViewedTable.update(it.copy(title = title).toOptional())
                }
                tableMap.update(result.map { it.id to it }.toMap())
            }
    }

    fun updateTableTheme(id: String, theme: TimetableColorTheme): Single<PutTableThemeResult> {
        return snuttRestApi.putTableTheme(id, PutTableThemeParams(theme))
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                storage.lastViewedTable.get().get()?.let {
                    storage.lastViewedTable.update(result.toOptional())
                }
            }
    }

    fun copyTable(id: String): Single<PostCopyTableResults> {
        return snuttRestApi.copyTable(id)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                tableMap.update(result.map { it.id to it }.toMap())
            }
    }
}
