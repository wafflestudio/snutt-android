package com.wafflestudio.snutt2.data.tables

import com.wafflestudio.snutt2.core.database.model.SimpleTable
import com.wafflestudio.snutt2.core.database.preference.SNUTTStorageTemp
import com.wafflestudio.snutt2.core.database.util.map
import com.wafflestudio.snutt2.core.database.util.toOptional
import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSource
import com.wafflestudio.snutt2.core.qualifiers.CoreDatabase
import com.wafflestudio.snutt2.core.qualifiers.CoreNetwork
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.network.dto.core.toDatabaseModel
import com.wafflestudio.snutt2.lib.network.dto.core.toExternalModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import com.wafflestudio.snutt2.core.network.model.PostTableParams as PostTableParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PutTableParams as PutTableParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PutTableThemeParams as PutTableThemeParamsNetwork

@Singleton
class TableRepositoryImpl @Inject constructor(
    @CoreNetwork private val api: SNUTTNetworkDataSource,
    @CoreDatabase private val snuttStorage: SNUTTStorageTemp,
    externalScope: CoroutineScope,
) : TableRepository {

    override val tableMap: StateFlow<Map<String, SimpleTableDto>> =
        snuttStorage.tableMap.asStateFlow()
            .map(externalScope) { it: Map<String,SimpleTable> ->
                it.mapValues { (_, value) -> value.toExternalModel() } // TODO : database 변환 사용 부분
            }

    override suspend fun fetchTableById(id: String) {
        val response = api._getTableById(id).toExternalModel()
        snuttStorage.lastViewedTable.update(response.toDatabaseModel().toOptional()) // TODO : database 변환 사용 부분
    }

    override suspend fun searchTableById(id: String): TableDto {
        return api._getTableById(id).toExternalModel()
    }

    override suspend fun fetchDefaultTable() {
        val response = api._getRecentTable().toExternalModel()
        snuttStorage.lastViewedTable.update(response.toDatabaseModel().toOptional()) // TODO : database 변환 사용 부분
    }

    override suspend fun getTableList(): List<SimpleTableDto> {
        val response = api._getTableList().map { it.toExternalModel() }
        snuttStorage.tableMap.update(response.map { it.toDatabaseModel() }.associateBy { it.id }) // TODO : database 변환 사용 부분
        return response
    }

    override suspend fun createTable(year: Long, semester: Long, title: String?) {
        val response = api._postTable(PostTableParamsNetwork(year, semester, title)).map { it.toExternalModel() }
        snuttStorage.tableMap.update(response.map { it.toDatabaseModel() }.associateBy { it.id }) // TODO : database 변환 사용 부분
        response
            .firstOrNull { it.year == year && it.semester == semester && it.title == title }
            ?.let {
                fetchTableById(it.id)
            }
    }

    override suspend fun deleteTable(id: String) {
        val response = api._deleteTable(id).map { it.toExternalModel() }
        snuttStorage.tableMap.update(response.map { it.toDatabaseModel() }.associateBy { it.id }) // TODO : database 변환 사용 부분
    }

    override suspend fun updateTableName(id: String, title: String) {
        val response = api._putTable(id, PutTableParamsNetwork(title)).map { it.toExternalModel() }
        snuttStorage.tableMap.update(response.map { it.toDatabaseModel() }.associateBy { it.id }) // TODO : database 변환 사용 부분
        val prev = snuttStorage.lastViewedTable.get().value
        snuttStorage.lastViewedTable.update(
            if (prev?.id == id) {
                prev.copy(title = title).toOptional()
            } else {
                prev.toOptional()
            },
        )
    }

    override suspend fun updateTableTheme(tableId: String, code: Int) {
        val response = api._putTableTheme(tableId, PutTableThemeParamsNetwork(theme = code)).toExternalModel()
        val prev = snuttStorage.lastViewedTable.get().value?.toExternalModel() // TODO : database 변환 사용 부분
        snuttStorage.lastViewedTable.update(
            if (prev?.id == tableId) {
                response.toDatabaseModel().toOptional() // TODO : database 변환 사용 부분
            } else {
                prev?.toDatabaseModel().toOptional() // TODO : database 변환 사용 부분
            },
        )
    }

    override suspend fun updateTableTheme(tableId: String, themeId: String) {
        val response = api._putTableTheme(tableId, PutTableThemeParamsNetwork(themeId = themeId)).toExternalModel()
        val prev = snuttStorage.lastViewedTable.get().value?.toExternalModel() // TODO : database 변환 사용 부분
        snuttStorage.lastViewedTable.update(
            if (prev?.id == tableId) {
                response.toDatabaseModel().toOptional() // TODO : database 변환 사용 부분
            } else {
                prev?.toDatabaseModel().toOptional() // TODO : database 변환 사용 부분
            },
        )
    }

    override suspend fun copyTable(id: String) {
        val response = api._copyTable(id).map { it.toExternalModel() }
        snuttStorage.tableMap.update(response.map { it.toDatabaseModel() }.associateBy { it.id }) // TODO : database 변환 사용 부분
    }

    override suspend fun setTablePrimary(id: String) {
        api._postPrimaryTable(id)
    }

    override suspend fun setTableNotPrimary(id: String) {
        api._deletePrimaryTable(id)
    }
}
