package com.wafflestudio.snutt2.data.tables

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.domainmodel.LectureReminderOffset
import com.wafflestudio.snutt2.domainmodel.LectureWithReminderOption
import com.wafflestudio.snutt2.domainmodel.TableSummary
import com.wafflestudio.snutt2.domainmodel.TimetableLectureReminders
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PostTableParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableThemeParams
import com.wafflestudio.snutt2.lib.network.dto.PutTimetableLectureReminderParams
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.network.dto.core.toDomainModel
import com.wafflestudio.snutt2.lib.network.toDomainError
import com.wafflestudio.snutt2.lib.toOptional
import com.wafflestudio.snutt2.domainmodel.toOffsetString
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
@Singleton
class TableRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val snuttStorage: SNUTTStorage,
) : TableRepository {

    override val currentTable: StateFlow<Table?>
        get() = object : StateFlow<Table?> {
            private val source = snuttStorage.lastViewedTable.asStateFlow()

            override val value: Table?
                get() = source.value.value?.let { Table.fromTableDto(it) }

            override val replayCache: List<Table?>
                get() = listOf(value)

            override suspend fun collect(collector: kotlinx.coroutines.flow.FlowCollector<Table?>): Nothing {
                source.collect { optionalDto ->
                    collector.emit(optionalDto.value?.let { Table.fromTableDto(it) })
                }
            }
        }

    override val tableSummaryList: StateFlow<List<TableSummary>>
        get() = object : StateFlow<List<TableSummary>> {
            private val source = snuttStorage.tableMap.asStateFlow()

            override val value: List<TableSummary>
                get() = source.value.values.map { dto ->
                    TableSummary.fromSimpleTableDto(dto)
                }

            override val replayCache: List<List<TableSummary>>
                get() = listOf(value)

            override suspend fun collect(collector: kotlinx.coroutines.flow.FlowCollector<List<TableSummary>>): Nothing {
                source.collect { dtoMap ->
                    collector.emit(
                        dtoMap.values.map { dto ->
                            TableSummary.fromSimpleTableDto(dto)
                        },
                    )
                }
            }
        }

    override suspend fun updateCurrentTable() {
        val prevTable = snuttStorage.lastViewedTable.get().value
            ?: return
        val response = api._getTableById(prevTable.id)
        snuttStorage.lastViewedTable.update(response.toOptional())
    }

    override suspend fun fetchAndSelectDefaultTable(): Result<Unit> {
        return try {
            val response = api._getRecentTable()
            snuttStorage.lastViewedTable.update(response.toOptional())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun fetchTableList(): Result<Unit> {
        try {
            val response = api._getTableList()
            snuttStorage.tableMap.update(response.associateBy { it.id })
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun fetchAndSelectTable(id: String): Result<Unit> {
        return try {
            val response = api._getTableById(id)
            snuttStorage.lastViewedTable.update(response.toOptional())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getTableById(id: String): Result<Table> {
        try {
            val dto = api._getTableById(id)
            return Result.Success(Table.fromTableDto(dto))
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun createAndSelectTable(courseBook: CourseBook, title: String): Result<Unit> {
        try {
            val response = api._postTable(
                PostTableParams(
                    year = courseBook.year,
                    semester = courseBook.semester,
                    title = title,
                ),
            )
            // FIXME: 데이터 레이어 갈아엎을 때 이 암묵적인 동작도 어떻게 좀 하기
            snuttStorage.tableMap.update(response.associateBy { it.id })
            response
                .firstOrNull { it.year == courseBook.year && it.semester == courseBook.semester && it.title == title }
                ?.let {
                    fetchAndSelectTable(it.id)
                }

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun updateTableName(table: TableSummary, newTitle: String): Result<Unit> {
        try {
            val response = api._putTable(
                id = table.id,
                PutTableParams(title = newTitle),
            )
            // FIXME: 데이터 레이어 갈아엎을 때 이 암묵적인 동작도 어떻게 좀 하기
            snuttStorage.tableMap.update(response.associateBy { it.id })
            val prev = snuttStorage.lastViewedTable.get().value
            snuttStorage.lastViewedTable.update(
                if (prev?.id == table.id) {
                    prev.copy(title = newTitle).toOptional()
                } else {
                    prev.toOptional()
                },
            )

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun setPrimaryTable(table: TableSummary): Result<Unit> {
        try {
            api._postPrimaryTable(table.id)
            refreshTableListAndCurrentTable()
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun unsetPrimaryTable(table: TableSummary): Result<Unit> {
        try {
            api._deletePrimaryTable(table.id)
            refreshTableListAndCurrentTable()
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    private suspend fun refreshTableListAndCurrentTable() {
        val tableListResponse = api._getTableList()
        snuttStorage.tableMap.update(tableListResponse.associateBy { it.id })

        val prevTable = snuttStorage.lastViewedTable.get().value ?: return
        val currentTableResponse = api._getTableById(prevTable.id)
        snuttStorage.lastViewedTable.update(currentTableResponse.toOptional())
    }

    override suspend fun deleteTable(table: TableSummary): Result<Unit> {
        try {
            val response = api._deleteTable(table.id)
            // FIXME: 데이터 레이어 수정
            snuttStorage.tableMap.update(response.associateBy { it.id })

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun copyTable(table: TableSummary): Result<Unit> {
        try {
            val response = api._copyTable(table.id)
            snuttStorage.tableMap.update(response.associateBy { it.id })
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun updateTableTheme(tableId: String, code: Int): Result<Unit> {
        return try {
            val response = api._putTableTheme(tableId, PutTableThemeParams(theme = code))
            val prev = snuttStorage.lastViewedTable.get().value
            snuttStorage.lastViewedTable.update(
                if (prev?.id == tableId) {
                    response.toOptional()
                } else {
                    prev.toOptional()
                },
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun updateTableTheme(tableId: String, themeId: String): Result<Unit> {
        return try {
            val response = api._putTableTheme(tableId, PutTableThemeParams(themeId = themeId))
            val prev = snuttStorage.lastViewedTable.get().value
            snuttStorage.lastViewedTable.update(
                if (prev?.id == tableId) {
                    response.toOptional()
                } else {
                    prev.toOptional()
                },
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getTimetableReminders(timetableId: String): Result<TimetableLectureReminders> {
        try {
            val timetableReminders = api._getTimetableReminders(timetableId)
            val reminderTable = api._getTableById(timetableId)
            val lecturesWithReminderOption = timetableReminders.map { it.toDomainModel() }
            val result = reminderTable.lectureList.map { lecture ->
                val matchingOption = lecturesWithReminderOption.find { it.lectureId == lecture.id }
                LectureWithReminderOption(
                    lectureId = lecture.id,
                    lectureTitle = lecture.course_title,
                    lectureReminderOffset = matchingOption?.lectureReminderOffset
                        ?: LectureReminderOffset.NONE,
                )
            }
            return Result.Success(TimetableLectureReminders(timetableId, result))
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getTimetableLectureReminder(
        timetableId: String,
        lectureId: String,
    ): Result<LectureWithReminderOption> {
        try {
            val result = api._getTimetableLectureReminder(timetableId, lectureId)
            return Result.Success(result.toDomainModel())
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun updateTimetableLectureReminder(
        timetableId: String,
        lectureId: String,
        offset: LectureReminderOffset,
    ): Result<LectureWithReminderOption> {
        try {
            val result = api._putTimetableLectureReminder(timetableId, lectureId, PutTimetableLectureReminderParams(offset.toOffsetString())).toDomainModel()
            return Result.Success(result)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }
}
