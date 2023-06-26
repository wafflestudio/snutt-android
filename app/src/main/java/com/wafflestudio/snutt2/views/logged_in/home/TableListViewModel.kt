package com.wafflestudio.snutt2.views.logged_in.home

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.course_books.CourseBookRepository
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
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
                table.year == courseBook.year && table.semester == courseBook.semester
            }
        }
    }

    val newSemesterNotify = tableRepository.tableMap.map { tableMap ->
        tableMap.values.none { table ->
            table.year == mostRecentCourseBook.first().year && table.semester == mostRecentCourseBook.first().semester
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

    suspend fun deleteTable(tableId: String) {
        if(currentTableRepository.currentTable
            .filterNotNull()
            .first().id == tableId
        ) {
            val newTableId: String
            val courseBook = CourseBookDto(tableMap.value[tableId]!!.semester, tableMap.value[tableId]!!.year)
            val siblingTables = tableListOfEachCourseBook.first()[courseBook]!!
            if(siblingTables.size > 1) {
                val index = siblingTables.indexOfFirst { it.id == tableId }
                newTableId = if(index == siblingTables.size - 1) {
                    siblingTables[index - 1].id
                } else {
                    siblingTables[index + 1].id
                }
            }
            else {
                val siblingCoursebooks = courseBooksWhichHaveTable.first()
                val index = siblingCoursebooks.indexOf(courseBook)
                newTableId = if(index == siblingCoursebooks.size - 1) {
                    tableListOfEachCourseBook.first()[siblingCoursebooks[index - 1]]!!.last().id
                } else {
                    tableListOfEachCourseBook.first()[siblingCoursebooks[index + 1]]!!.first().id
                }
            }
            changeSelectedTable(newTableId)
        }
        return tableRepository.deleteTable(tableId)
    }

    suspend fun createTable(courseBook: CourseBookDto, tableName: String) {
        tableRepository.createTable(
            year = courseBook.year, semester = courseBook.semester, title = tableName
        )
    }

    suspend fun copyTable(tableId: String) {
        tableRepository.copyTable(tableId)
    }

    suspend fun checkTableDeletable(tableId: String): Boolean {
        return currentTableRepository.currentTable
            .filterNotNull()
            .map {
                it.id != tableId
            }.first()
    }

    suspend fun checkTableThemeChangeable(tableId: String): Boolean {
        return currentTableRepository.currentTable
            .filterNotNull()
            .map {
                it.id == tableId
            }.first()
    }
}
