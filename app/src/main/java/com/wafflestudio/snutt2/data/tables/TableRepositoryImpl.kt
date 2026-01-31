package com.wafflestudio.snutt2.data.tables

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.CourseBook
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
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.network.dto.core.toDomainModel
import com.wafflestudio.snutt2.lib.network.toDomainError
import com.wafflestudio.snutt2.lib.toOptional
import com.wafflestudio.snutt2.domainmodel.toOffsetString
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

    override suspend fun searchTableById(id: String): TableDto {
        return api._getTableById(id)
    }

    override suspend fun fetchDefaultTable() {
        val response = api._getRecentTable()
        snuttStorage.lastViewedTable.update(response.toOptional())
    }

    override suspend fun getTableList(): Result<List<SimpleTableDto>> {
        try {
            val response = api._getTableList()
            snuttStorage.tableMap.update(response.associateBy { it.id })
            return Result.Success(response)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
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

    override suspend fun updateTableTheme(tableId: String, code: Int) {
        val response = api._putTableTheme(tableId, PutTableThemeParams(theme = code))
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
        val response = api._putTableTheme(tableId, PutTableThemeParams(themeId = themeId))
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
        val response = api._copyTable(id)
        snuttStorage.tableMap.update(response.associateBy { it.id })
    }

    override suspend fun setTablePrimary(id: String) {
        api._postPrimaryTable(id)
        snuttStorage.tableMap.update(
            tableMap.value.mapValues { (key, table) ->
                if (key == id) {
                    table.copy(isPrimary = true)
                } else {
                    table
                }
            },
        )
    }

    override suspend fun setTableNotPrimary(id: String) {
        api._deletePrimaryTable(id)
        snuttStorage.tableMap.update(
            tableMap.value.mapValues { (key, table) ->
                if (key == id) {
                    table.copy(isPrimary = false)
                } else {
                    table
                }
            },
        )
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

    // 여기부터 리팩토링 코드
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

    override suspend fun createTableNew(courseBook: CourseBook, title: String): Result<Unit> {
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
                    fetchTableById(it.id)
                }

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun updateTableNameNew(newTitle: String, tableId: String): Result<Unit> {
        try {
            val response = api._putTable(
                id = tableId,
                PutTableParams(title = newTitle),
            )
            // FIXME: 데이터 레이어 갈아엎을 때 이 암묵적인 동작도 어떻게 좀 하기
            snuttStorage.tableMap.update(response.associateBy { it.id })
            val prev = snuttStorage.lastViewedTable.get().value
            snuttStorage.lastViewedTable.update(
                if (prev?.id == tableId) {
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

    override suspend fun setPrimaryTableNew(id: String): Result<Unit> {
        try {
            api._postPrimaryTable(id)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun unsetPrimaryTableNew(id: String): Result<Unit> {
        try {
            api._deletePrimaryTable(id)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun deleteTableNew(tableId: String): Result<Unit> {
        try {
            val response = api._deleteTable(tableId)
            // FIXME: 데이터 레이어 수정
            snuttStorage.tableMap.update(response.associateBy { it.id })

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun copyTableNew(id: String): Result<Unit> {
        try {
            val response = api._copyTable(id)
            snuttStorage.tableMap.update(response.associateBy { it.id })
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }
}
