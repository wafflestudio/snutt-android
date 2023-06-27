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

    suspend fun deleteTable(tableId: String) {
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

    fun checkTableDeletable(tableId: String): Boolean {
        val tableToDelete = tableRepository.tableMap.value[tableId] ?: return false
        if (currentTableRepository.currentTable.value?.id != tableId) return true
        return tableRepository.tableMap.value.values.filter {
            it.courseBookEquals(tableToDelete)
        }.size > 1
    }

    fun getNextSelectedTable(tableId: String): String? {
        return if (currentTableRepository.currentTable.value?.id == tableId) {
            val tableToDelete = tableMap.value[tableId] ?: return null
            val siblingTables = tableMap.value.values.filter { it.courseBookEquals(tableToDelete) }
            if (siblingTables.size > 1) {
                val index = siblingTables.indexOfFirst { it.id == tableId }
                if (index == siblingTables.size - 1) {
                    siblingTables[index - 1].id
                } else {
                    siblingTables[index + 1].id
                }
            } else {
                null
            }
        } else {
            null
        }
    }

    suspend fun checkTableThemeChangeable(tableId: String): Boolean {
        return currentTableRepository.currentTable
            .filterNotNull()
            .map {
                it.id == tableId
            }.first()
    }
}
