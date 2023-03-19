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
