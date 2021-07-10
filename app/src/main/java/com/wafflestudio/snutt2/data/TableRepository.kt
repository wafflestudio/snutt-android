package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.GetTableListResults
import com.wafflestudio.snutt2.lib.network.dto.PostTableParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableParams
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.toOptional
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableRepository @Inject constructor(
    private val snuttRestApi: SNUTTRestApi,
    private val storage: SNUTTStorage,
    private val apiOnError: ApiOnError
) {
    private val tableMap = BehaviorSubject.createDefault<Map<String, TableDto>>(mapOf())

    fun refreshTable(tableId: String): Single<TableDto> {
        val token: String = storage.accessToken.getValue()
        return snuttRestApi.getTableById(token, tableId)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                storage.lastViewedTable.setValue(it.toOptional())
            }
    }

    fun getDefaultTable(): Single<TableDto> {
        val token: String = storage.accessToken.getValue()
        return snuttRestApi.getRecentTable(token)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                storage.lastViewedTable.setValue(it.toOptional())

            }

    }

    fun getTableList(): Single<GetTableListResults> {
        val token: String = storage.accessToken.getValue()
        return snuttRestApi.getTableList(token)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                tableMap.onNext(result.map { it.id to it }.toMap())
            }
    }

    fun createTable(year: Long, semester: Long, title: String?): Single<List<TableDto>> {
        val token: String = storage.accessToken.getValue()
        return snuttRestApi.postTable(token, PostTableParams(year, semester, title))
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                tableMap.onNext(result.map { it.id to it }.toMap())
            }
    }

    fun deleteTable(id: String): Single<List<TableDto>> {
        val token: String = storage.accessToken.getValue()
        return snuttRestApi.deleteTable(token, id)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                tableMap.onNext(result.map { it.id to it }.toMap())
            }
    }

    fun putTable(id: String, title: String): Single<List<TableDto>> {
        val token: String = storage.accessToken.getValue()
        return snuttRestApi.putTable(token, id, PutTableParams(title))
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                tableMap.onNext(result.map { it.id to it }.toMap())
            }
    }
}
