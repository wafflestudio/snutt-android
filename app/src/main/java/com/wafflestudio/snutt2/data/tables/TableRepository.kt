package com.wafflestudio.snutt2.data.tables

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.LectureReminderOffset
import com.wafflestudio.snutt2.domainmodel.LectureWithReminderOption
import com.wafflestudio.snutt2.domainmodel.TableSummary
import com.wafflestudio.snutt2.domainmodel.TimetableLectureReminders
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import kotlinx.coroutines.flow.StateFlow

interface TableRepository {

    val tableMap: StateFlow<Map<String, SimpleTableDto>>

    suspend fun fetchTableById(id: String)

    suspend fun searchTableById(id: String): TableDto

    suspend fun fetchDefaultTable()

    suspend fun getTableList(): Result<List<SimpleTableDto>>

    suspend fun createTable(year: Long, semester: Long, title: String?)

    suspend fun deleteTable(id: String)

    suspend fun updateTableName(id: String, title: String)

    suspend fun updateTableTheme(tableId: String, code: Int)

    suspend fun updateTableTheme(tableId: String, themeId: String)

    suspend fun copyTable(id: String)

    suspend fun setTablePrimary(id: String)

    suspend fun setTableNotPrimary(id: String)

    suspend fun getTimetableReminders(timetableId: String): Result<TimetableLectureReminders>

    suspend fun getTimetableLectureReminder(
        timetableId: String,
        lectureId: String,
    ): Result<LectureWithReminderOption>

    suspend fun updateTimetableLectureReminder(
        timetableId: String,
        lectureId: String,
        offset: LectureReminderOffset,
    ): Result<LectureWithReminderOption>

    // 여기부터 리팩토링 코드
    val tableSummaryList: StateFlow<List<TableSummary>>
    suspend fun createTableNew(courseBook: CourseBook, title: String): Result<Unit>
    suspend fun updateTableNameNew(newTitle: String, tableId: String): Result<Unit>
    suspend fun setPrimaryTableNew(id: String): Result<Unit>

    suspend fun unsetPrimaryTableNew(id: String): Result<Unit>
    suspend fun deleteTableNew(tableId: String): Result<Unit>
}
