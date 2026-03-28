package com.wafflestudio.snutt2.data.tables

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.LectureReminderOffset
import com.wafflestudio.snutt2.domainmodel.LectureWithReminderOption
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.domainmodel.TableSummary
import com.wafflestudio.snutt2.domainmodel.TimetableLectureReminders
import com.wafflestudio.snutt2.lib.network.Result
import kotlinx.coroutines.flow.StateFlow

interface TableRepository {

    val currentTable: StateFlow<Table?>

    val tableSummaryList: StateFlow<List<TableSummary>>

    suspend fun updateCurrentTable()

    suspend fun fetchAndSelectDefaultTable(): Result<Unit>

    suspend fun fetchTableList(): Result<Unit>

    suspend fun fetchAndSelectTable(id: String): Result<Unit>

    suspend fun getTableById(id: String): Result<Table>

    suspend fun createAndSelectTable(courseBook: CourseBook, title: String): Result<Unit>

    suspend fun updateTableName(newTitle: String, tableId: String): Result<Unit>

    suspend fun setPrimaryTable(id: String): Result<Unit>

    suspend fun unsetPrimaryTable(id: String): Result<Unit>

    suspend fun deleteTable(tableId: String): Result<Unit>

    suspend fun copyTable(id: String): Result<Unit>

    suspend fun updateTableTheme(tableId: String, code: Int): Result<Unit>

    suspend fun updateTableTheme(tableId: String, themeId: String): Result<Unit>

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
}
