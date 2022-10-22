@file:OptIn(ExperimentalCoroutinesApi::class)

package com.wafflestudio.snutt2.views.logged_in.home

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.course_books.CourseBookRepository
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TableListViewModelNew @Inject constructor(
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

    suspend fun changeSelectedTableNew(tableId: String) {
        tableRepository.fetchTableById(tableId)
    }

    suspend fun changeNameTableNew(tableId: String, name: String) {
        return tableRepository.updateTableName(tableId, name)
    }

    suspend fun deleteTableNew(tableId: String) {
        return tableRepository.deleteTable(tableId)
    }

    suspend fun createTableNew(courseBook: CourseBookDto, tableName: String) {
        tableRepository.createTable(
            year = courseBook.year, semester = courseBook.semester, title = tableName
        )
    }

    suspend fun copyTableNew(tableId: String) {
        tableRepository.copyTable(tableId)
    }

    suspend fun checkTableDeletableNew(tableId: String): Boolean {
        return currentTableRepository.currentTable
            .filterNotNull()
            .map {
                it.id != tableId
            }.first()
    }

    suspend fun checkTableThemeChangeableNew(tableId: String): Boolean {
        return currentTableRepository.currentTable
            .filterNotNull()
            .map {
                it.id == tableId
            }.first()
    }
}
