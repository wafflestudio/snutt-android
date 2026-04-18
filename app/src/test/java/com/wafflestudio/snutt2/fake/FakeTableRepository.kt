package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.domain.model.Table
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.domain.model.TimetableLectureReminders
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTableRepository : TableRepository {

    // --- StateFlow ---
    override val currentTable = MutableStateFlow<Table?>(null)
    override val tableSummaryList = MutableStateFlow<List<TableSummary>>(emptyList())

    // --- 테스트 제어용 필드 ---
    var fetchAndSelectTableResult: Result<Unit> = Result.Success(Unit)
    var fetchAndSelectTableCalledWith: String? = null
        private set

    var createAndSelectTableResult: Result<Unit> = Result.Success(Unit)
    var createAndSelectTableCalledWith: Pair<CourseBook, String>? = null
        private set

    var updateTableNameResult: Result<Unit> = Result.Success(Unit)
    var updateTableNameCalledWith: Pair<TableSummary, String>? = null
        private set

    var deleteTableResult: Result<Unit> = Result.Success(Unit)
    var deleteTableCalledWith: TableSummary? = null
        private set

    var copyTableResult: Result<Unit> = Result.Success(Unit)
    var copyTableCalledWith: TableSummary? = null
        private set

    var getTableByIdResult: Result<Table> = Result.Fail(
        com.wafflestudio.snutt2.domain.Unknown(displayTitle = "", displayMessage = ""),
    )
    var getTableByIdCalledWith: String? = null
        private set

    var setPrimaryTableResult: Result<Unit> = Result.Success(Unit)
    var setPrimaryTableCalledWith: TableSummary? = null
        private set

    var unsetPrimaryTableResult: Result<Unit> = Result.Success(Unit)
    var unsetPrimaryTableCalledWith: TableSummary? = null
        private set

    var fetchTableListResult: Result<Unit> = Result.Success(Unit)
    var fetchTableListCalled = false
        private set

    var updateTableThemeBuiltInResult: Result<Unit> = Result.Success(Unit)
    var updateTableThemeBuiltInCalledWith: Pair<String, Int>? = null
        private set

    var updateTableThemeCustomResult: Result<Unit> = Result.Success(Unit)
    var updateTableThemeCustomCalledWith: Pair<String, String>? = null
        private set

    // --- 인터페이스 구현 ---
    override suspend fun fetchAndSelectTable(id: String): Result<Unit> {
        fetchAndSelectTableCalledWith = id
        return fetchAndSelectTableResult
    }

    override suspend fun createAndSelectTable(courseBook: CourseBook, title: String): Result<Unit> {
        createAndSelectTableCalledWith = courseBook to title
        return createAndSelectTableResult
    }

    override suspend fun updateTableName(table: TableSummary, newTitle: String): Result<Unit> {
        updateTableNameCalledWith = table to newTitle
        return updateTableNameResult
    }

    override suspend fun deleteTable(table: TableSummary): Result<Unit> {
        deleteTableCalledWith = table
        return deleteTableResult
    }

    override suspend fun copyTable(table: TableSummary): Result<Unit> {
        copyTableCalledWith = table
        return copyTableResult
    }

    override suspend fun getTableById(id: String): Result<Table> {
        getTableByIdCalledWith = id
        return getTableByIdResult
    }

    override suspend fun setPrimaryTable(table: TableSummary): Result<Unit> {
        setPrimaryTableCalledWith = table
        return setPrimaryTableResult
    }

    override suspend fun unsetPrimaryTable(table: TableSummary): Result<Unit> {
        unsetPrimaryTableCalledWith = table
        return unsetPrimaryTableResult
    }

    override suspend fun fetchTableList(): Result<Unit> {
        fetchTableListCalled = true
        return fetchTableListResult
    }

    override suspend fun updateTableTheme(tableId: String, code: Int): Result<Unit> {
        updateTableThemeBuiltInCalledWith = tableId to code
        return updateTableThemeBuiltInResult
    }

    override suspend fun updateTableTheme(tableId: String, themeId: String): Result<Unit> {
        updateTableThemeCustomCalledWith = tableId to themeId
        return updateTableThemeCustomResult
    }

    // --- 미사용 메서드 ---
    override suspend fun updateCurrentTable() = TODO("Not used in this test")
    override suspend fun fetchAndSelectDefaultTable(): Result<Unit> = TODO("Not used in this test")
    override suspend fun getTimetableReminders(timetableId: String): Result<TimetableLectureReminders> = TODO("Not used in this test")
    override suspend fun getTimetableLectureReminder(timetableId: String, lectureId: String): Result<LectureWithReminderOption> = TODO("Not used in this test")
    override suspend fun updateTimetableLectureReminder(timetableId: String, lectureId: String, offset: LectureReminderOffset): Result<LectureWithReminderOption> = TODO("Not used in this test")
}
