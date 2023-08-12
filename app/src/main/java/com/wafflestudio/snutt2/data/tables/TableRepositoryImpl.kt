package com.wafflestudio.snutt2.data.tables

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PostTableParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableThemeParams
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.toOptional
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val snuttStorage: SNUTTStorage,
) : TableRepository {

    override val tableMap: StateFlow<Map<String, SimpleTableDto>> =
        snuttStorage.tableMap.asStateFlow()

    override suspend fun fetchTableById(id: String) {
        val response = api._getTableById(id)
        snuttStorage.lastViewedTable.update(response.toOptional())
    }

    override suspend fun fetchDefaultTable() {
        val response = api._getRecentTable()
        snuttStorage.lastViewedTable.update(response.toOptional())
    }

    override suspend fun getTableList(): List<SimpleTableDto> {
        val response = api._getTableList()
        snuttStorage.tableMap.update(response.associateBy { it.id })
        return response
    }

    override suspend fun createTable(year: Long, semester: Long, title: String?) {
        val response = api._postTable(PostTableParams(year, semester, title))
        snuttStorage.tableMap.update(response.associateBy { it.id })
        response
            .firstOrNull { it.year == year && it.semester == semester && it.title == title }
            ?.let {
                fetchTableById(it.id)
            }
    }

    override suspend fun deleteTable(id: String) {
        val response = api._deleteTable(id)
        snuttStorage.tableMap.update(response.associateBy { it.id })
    }

    override suspend fun updateTableName(id: String, title: String) {
        val response = api._putTable(id, PutTableParams(title))
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

    override suspend fun updateTableTheme(id: String, theme: TimetableColorTheme) {
        val response = api._putTableTheme(id, PutTableThemeParams(theme))
        val prev = snuttStorage.lastViewedTable.get().value
        snuttStorage.lastViewedTable.update(
            if (prev?.id == id) {
                response.toOptional()
            } else {
                prev.toOptional()
            },
        )
    }

    override suspend fun copyTable(id: String) {
        val response = api._copyTable(id)
        snuttStorage.tableMap.update(response.associateBy { it.id })
    }
}
