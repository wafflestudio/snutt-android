package com.wafflestudio.snutt2.data.tables

import androidx.datastore.core.DataStore
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PostTableParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableThemeParams
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.storage.CurrentTablePreferences
import com.wafflestudio.snutt2.lib.storage.TableMapPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val tableMapStore: DataStore<TableMapPreferences>,
    private val currentTableStore: DataStore<CurrentTablePreferences>
) : TableRepository {

    override val tableMap: Flow<Map<String, SimpleTableDto>> =
        tableMapStore.data
            .map { it.map }
            .distinctUntilChanged()

    override suspend fun fetchTableById(id: String) {
        val response = api._getTableById(id)
        currentTableStore.updateData {
            it.copy(data = response)
        }
    }

    override suspend fun fetchDefaultTable() {
        val response = api._getRecentTable()
        currentTableStore.updateData {
            it.copy(data = response)
        }
    }

    override suspend fun getTableList(): List<SimpleTableDto> {
        return api._getTableList()
    }

    override suspend fun createTable(year: Long, semester: Long, title: String?) {
        val response = api._postTable(PostTableParams(year, semester, title))
        tableMapStore.updateData {
            TableMapPreferences(response.map { it.id to it }.toMap())
        }
    }

    override suspend fun deleteTable(id: String) {
        val response = api._deleteTable(id)
        tableMapStore.updateData {
            TableMapPreferences(response.map { it.id to it }.toMap())
        }
    }

    override suspend fun updateTableName(id: String, title: String) {
        val response = api._putTable(id, PutTableParams(title))
        tableMapStore.updateData {
            TableMapPreferences(response.map { it.id to it }.toMap())
        }
        currentTableStore.updateData { prev ->
            if (prev.data?.id == id) {
                CurrentTablePreferences(prev.data.copy(title = title))
            } else {
                prev
            }
        }
    }

    override suspend fun updateTableTheme(id: String, theme: TimetableColorTheme) {
        val response = api._putTableTheme(id, PutTableThemeParams(theme))
        currentTableStore.updateData { prev ->
            if (prev.data?.id == response.id) {
                CurrentTablePreferences(response)
            } else {
                prev
            }
        }
    }

    override suspend fun copyTable(id: String) {
        val response = api._copyTable(id)
        tableMapStore.updateData {
            TableMapPreferences(response.associateBy { it.id })
        }
    }
}
