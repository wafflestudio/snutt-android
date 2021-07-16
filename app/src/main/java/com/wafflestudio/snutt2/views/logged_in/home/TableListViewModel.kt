package com.wafflestudio.snutt2.views.logged_in.home

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.CourseBookRepository
import com.wafflestudio.snutt2.data.TableRepository
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@HiltViewModel
class TableListViewModel @Inject constructor(
    private val tableRepository: TableRepository,
    private val courseBookRepository: CourseBookRepository,
    private val apiOnError: ApiOnError,
) : ViewModel() {
    private val selectedCourseBooksSubject = BehaviorSubject.createDefault(
        tableRepository.getCurrentTable()?.let { CourseBookDto(it.semester, it.year) })

    val selectedCourseBooks: Observable<CourseBookDto> = selectedCourseBooksSubject.hide()
    val courseBooks = courseBookRepository.courseBooks
    private val tableMap = tableRepository.tableMap

    val currentCourseBooksTable: Observable<List<TableDto>> =
        Observable.combineLatest(tableMap, selectedCourseBooksSubject, { map, courseBook ->
            map.toList().map { it.second }.filter { table ->
                table.semester == courseBook.semester && table.year == courseBook.year
            }
        })

    fun refresh() {
        tableRepository.getTableList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = apiOnError)
        courseBookRepository.loadCourseBook()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = apiOnError)
    }

    fun selectCurrentCourseBook(courseBookDto: CourseBookDto) {
        courseBooks.firstElement()
            .subscribeBy {
                selectedCourseBooksSubject.onNext(courseBookDto)
            }
    }

    fun changeCurrentTable(tableId: String) {
        tableRepository.refreshTable(tableId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = apiOnError)
    }

    fun createTable(tableName: String) {
        val currentCourseBook = selectedCourseBooksSubject.value
        tableRepository.createTable(currentCourseBook.year, currentCourseBook.semester, tableName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = apiOnError)
    }

    fun copyTable(tableId: String) {
        tableRepository.copyTable(tableId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = apiOnError)
    }

    fun deleteTable(tableId: String) {
        tableRepository.deleteTable(tableId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = apiOnError)
    }

    fun changeNameTable(tableId: String, name: String) {
        tableRepository.putTable(tableId, name)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = apiOnError)
    }
}
