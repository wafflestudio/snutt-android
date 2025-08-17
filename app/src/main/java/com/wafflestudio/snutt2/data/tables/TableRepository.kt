package com.wafflestudio.snutt2.data.tables

import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.views.logged_in.home.settings.LectureWithReminderOption
import kotlinx.coroutines.flow.StateFlow

interface TableRepository {

    val tableMap: StateFlow<Map<String, SimpleTableDto>>

    suspend fun fetchTableById(id: String)

    suspend fun searchTableById(id: String): TableDto

    suspend fun fetchDefaultTable()

    suspend fun getTableList(): List<SimpleTableDto>

    suspend fun createTable(year: Long, semester: Long, title: String?)

    suspend fun deleteTable(id: String)

    suspend fun updateTableName(id: String, title: String)

    suspend fun updateTableTheme(tableId: String, code: Int)

    suspend fun updateTableTheme(tableId: String, themeId: String)

    suspend fun copyTable(id: String)

    suspend fun setTablePrimary(id: String)

    suspend fun setTableNotPrimary(id: String)

    // TODO: 임시 코드
    suspend fun getPrimaryTableLectureReminders(): Result<List<LectureWithReminderOption>>

    // TODO: 임시 코드
    suspend fun updateTableLectureReminders(lectureId: String, newOption: LectureWithReminderOption): Result<Unit>
}
