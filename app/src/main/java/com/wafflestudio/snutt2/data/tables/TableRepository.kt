package com.wafflestudio.snutt2.data.tables

import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.network.dto.core.SharedTableDto
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleSharedTableDto
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import kotlinx.coroutines.flow.StateFlow

interface TableRepository {

    val tableMap: StateFlow<Map<String, SimpleTableDto>>

    suspend fun fetchTableById(id: String)

    suspend fun fetchDefaultTable()

    suspend fun getTableList(): List<SimpleTableDto>

    suspend fun createTable(year: Long, semester: Long, title: String?)

    suspend fun deleteTable(id: String)

    suspend fun updateTableName(id: String, title: String)

    suspend fun updateTableTheme(id: String, theme: TimetableColorTheme)

    suspend fun copyTable(id: String)

    suspend fun fetchSharedTableList(): List<SimpleSharedTableDto>

    suspend fun getSharedTableById(tableId: String): SharedTableDto

    suspend fun createSharedTable(title: String, tableId: String)

    suspend fun updateSharedTableTitle(tableId: String, title: String)

    suspend fun deleteSharedTable(tableId: String)

    suspend fun copySharedTable(tableId: String)

    suspend fun createShareLink(tableId: String): String
}
