package com.wafflestudio.snutt2.views.logged_in.home

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.course_books.CourseBookRepository
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.lib.courseBookEquals
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TableListViewModel @Inject constructor(
    private val courseBookRepository: CourseBookRepository,
    private val tableRepository: TableRepository,
    private val currentTableRepository: CurrentTableRepository,
) : ViewModel() {

    private val _allCourseBook = MutableStateFlow(emptyList<CourseBookDto>())
    val allCourseBook = _allCourseBook.asStateFlow()
    private val mostRecentCourseBook = _allCourseBook.filter { it.isNotEmpty() }.map { it.first() }

    private val tableMap = tableRepository.tableMap

    val courseBooksWhichHaveTable = tableMap.map {
        (
            it.values.map { table ->
                CourseBookDto(year = table.year, semester = table.semester)
            } + mostRecentCourseBook.first()
            ).distinct().sorted()
    }

    val tableListOfEachCourseBook = courseBooksWhichHaveTable.map {
        it.associateWith { courseBook ->
            tableMap.value.values.filter { table ->
                table.courseBookEquals(courseBook)
            }
        }
    }

    val newSemesterNotify = tableRepository.tableMap.map { tableMap ->
        tableMap.values.none { table ->
            table.courseBookEquals(mostRecentCourseBook.first())
        }
    }

    suspend fun fetchCourseBooks() {
        _allCourseBook.emit(courseBookRepository.getCourseBook())
    }

    suspend fun fetchTableMap() {
        tableRepository.getTableList()
    }

    suspend fun changeSelectedTable(tableId: String) {
        tableRepository.fetchTableById(tableId)
    }

    suspend fun changeTableName(tableId: String, name: String) {
        return tableRepository.updateTableName(tableId, name)
    }

    suspend fun createTable(courseBook: CourseBookDto, tableName: String) {
        tableRepository.createTable(
            year = courseBook.year, semester = courseBook.semester, title = tableName,
        )
    }

    suspend fun copyTable(tableId: String) {
        tableRepository.copyTable(tableId)
    }

    fun checkTableDeletable(): Boolean {
        return tableMap.value.size > 1
    }

    suspend fun deleteTableAndSwitchIfNeeded(tableId: String) { // 시간표를 삭제하고, 현재 시간표라면 index를 유지하며 다른 시간표를 선택한다
        val tableToDelete = tableMap.value[tableId] ?: return
        val siblingTables = tableMap.map { it.values.filter { table -> table.courseBookEquals(tableToDelete) } }
        val indexInSibling = siblingTables.first().indexOfFirst { it.id == tableId }
        val tables = tableListOfEachCourseBook.map { it.values.flatten() }
        val index = tables.first().indexOfFirst { it.id == tableId }

        tableRepository.deleteTable(tableId)

        if (currentTableRepository.currentTable.value?.id == tableId) {
            if (siblingTables.first().isEmpty()) {
                if (index == tables.first().size) {
                    changeSelectedTable(tables.first().last().id)
                } else {
                    changeSelectedTable(tables.first()[index].id)
                }
            } else {
                if (indexInSibling == siblingTables.first().size) {
                    changeSelectedTable(siblingTables.first().last().id)
                } else {
                    changeSelectedTable(siblingTables.first()[indexInSibling].id)
                }
            }
        }
    }

    suspend fun checkTableThemeChangeable(tableId: String): Boolean {
        return currentTableRepository.currentTable
            .filterNotNull()
            .map {
                it.id == tableId
            }.first()
    }

    suspend fun setTablePrimary(tableId: String) {
        tableRepository.setTablePrimary(tableId)
    }

    suspend fun setTableNotPrimary(tableId: String) {
        tableRepository.setTableNotPrimary(tableId)
    }
}
