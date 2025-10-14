package com.wafflestudio.snutt2.data.tables

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.LectureWithReminderOption
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
import com.wafflestudio.snutt2.domainmodel.TimetableLectureReminders
import com.wafflestudio.snutt2.domainmodel.getStringOffset
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
            val result = reminderTable.lectureList.mapNotNull { lecture ->
                val matchingOption = lecturesWithReminderOption.find { it.lectureId == lecture.id }
                matchingOption?.let {
                    LectureWithReminderOption(
                        lectureId = lecture.id,
                        lectureTitle = lecture.course_title,
                        lectureReminderOffset = it.lectureReminderOffset,
                    )
                }
            }
            return Result.Success(TimetableLectureReminders(timetableId, result))
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getTimetableLectureReminder(timetableId: String, lectureId: String): Result<LectureWithReminderOption> {
        try {
            val result = api._getTimetableLectureReminder(timetableId, lectureId)
            return Result.Success(result.toDomainModel())
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun updateTimetableLectureReminder(timetableId: String, lectureId: String, option: LectureWithReminderOption): Result<LectureWithReminderOption> {
        try {
            val result = api._putTimetableLectureReminder(timetableId, lectureId, PutTimetableLectureReminderParams(option.lectureReminderOffset.getStringOffset())).toDomainModel()
            return Result.Success(result)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }
}
