package com.wafflestudio.snutt2.data.tables

import com.wafflestudio.snutt2.core.data.toNetworkModel
import com.wafflestudio.snutt2.core.data.toSimpleTableTempModel
import com.wafflestudio.snutt2.core.data.toTempModel
import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSource
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.data.toExternalModel
import com.wafflestudio.snutt2.data.toSimpleTableExternalModel
import com.wafflestudio.snutt2.data.toTempModel
import com.wafflestudio.snutt2.lib.network.dto.PostTableParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableThemeParams
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.toOptional
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableRepositoryImpl @Inject constructor(
    private val api: SNUTTNetworkDataSource,
    private val snuttStorage: SNUTTStorage,
) : TableRepository {

    override val tableMap: StateFlow<Map<String, SimpleTableDto>> =
        snuttStorage.tableMap.asStateFlow()

    override suspend fun fetchTableById(id: String) {
        val response = api._getTableById(id).toTempModel().toExternalModel() // TODO : 변환 함수 사용 부분
        snuttStorage.lastViewedTable.update(response.toOptional())
    }

    override suspend fun searchTableById(id: String): TableDto {
        return api._getTableById(id).toTempModel().toExternalModel() // TODO : 변환 함수 사용 부분
    }

    override suspend fun fetchDefaultTable() {
        val response = api._getRecentTable().toTempModel().toExternalModel() // TODO : 변환 함수 사용 부분
        snuttStorage.lastViewedTable.update(response.toOptional())
    }

    override suspend fun getTableList(): List<SimpleTableDto> {
        val response = api._getTableList().toSimpleTableTempModel().toSimpleTableExternalModel() // TODO : 변환 함수 사용 부분
        snuttStorage.tableMap.update(response.associateBy { it.id })
        return response
    }

    override suspend fun createTable(year: Long, semester: Long, title: String?) {
        val response = api._postTable(PostTableParams(year, semester, title).toTempModel().toNetworkModel())
            .toSimpleTableTempModel().toSimpleTableExternalModel() // TODO : 변환 함수 사용 부분
        snuttStorage.tableMap.update(response.associateBy { it.id })
        response
            .firstOrNull { it.year == year && it.semester == semester && it.title == title }
            ?.let {
                fetchTableById(it.id)
            }
    }

    override suspend fun deleteTable(id: String) {
        val response = api._deleteTable(id).toSimpleTableTempModel().toSimpleTableExternalModel() // TODO : 변환 함수 사용 부분
        snuttStorage.tableMap.update(response.associateBy { it.id })
    }

    override suspend fun updateTableName(id: String, title: String) {
        val response = api._putTable(id, PutTableParams(title).toTempModel().toNetworkModel())
            .toSimpleTableTempModel().toSimpleTableExternalModel() // TODO : 변환 함수 사용 부분
        snuttStorage.tableMap.update(response.associateBy { it.id })
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
        val response = api._putTableTheme(tableId, PutTableThemeParams(theme = code).toTempModel().toNetworkModel())
            .toTempModel().toExternalModel() // TODO : 변환 함수 사용 부분
        val prev = snuttStorage.lastViewedTable.get().value
        snuttStorage.lastViewedTable.update(
            if (prev?.id == tableId) {
                response.toOptional()
            } else {
                prev.toOptional()
            },
        )
    }

    override suspend fun updateTableTheme(tableId: String, themeId: String) {
        val response = api._putTableTheme(tableId, PutTableThemeParams(themeId = themeId).toTempModel().toNetworkModel())
            .toTempModel().toExternalModel() // TODO : 변환 함수 사용 부분
        val prev = snuttStorage.lastViewedTable.get().value
        snuttStorage.lastViewedTable.update(
            if (prev?.id == tableId) {
                response.toOptional()
            } else {
                prev.toOptional()
            },
        )
    }

    override suspend fun copyTable(id: String) {
        val response = api._copyTable(id).toSimpleTableTempModel().toSimpleTableExternalModel()// TODO : 변환 함수 사용 부분
        snuttStorage.tableMap.update(response.associateBy { it.id })
    }

    override suspend fun setTablePrimary(id: String) {
        api._postPrimaryTable(id)
    }

    override suspend fun setTableNotPrimary(id: String) {
        api._deletePrimaryTable(id)
    }
}
