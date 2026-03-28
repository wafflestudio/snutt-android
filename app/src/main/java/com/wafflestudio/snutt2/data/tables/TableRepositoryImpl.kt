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
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val snuttStorage: SNUTTStorage,
) : TableRepository {

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

    override suspend fun fetchDefaultTable(): Result<Unit> {
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

    override suspend fun fetchTableById(id: String): Result<Unit> {
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

    override suspend fun createTable(courseBook: CourseBook, title: String): Result<Unit> {
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

    override suspend fun updateTableName(newTitle: String, tableId: String): Result<Unit> {
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

    override suspend fun setPrimaryTable(id: String): Result<Unit> {
        try {
            api._postPrimaryTable(id)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun unsetPrimaryTable(id: String): Result<Unit> {
        try {
            api._deletePrimaryTable(id)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun deleteTable(tableId: String): Result<Unit> {
        try {
            val response = api._deleteTable(tableId)
            // FIXME: 데이터 레이어 수정
            snuttStorage.tableMap.update(response.associateBy { it.id })

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun copyTable(id: String): Result<Unit> {
        try {
            val response = api._copyTable(id)
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
