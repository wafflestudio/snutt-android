package com.wafflestudio.snutt2.views.logged_in.home

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.CourseBookRepository
import com.wafflestudio.snutt2.data.TableRepository
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.lib.toOptional
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
    private val selectedCourseBooksSubject =
        BehaviorSubject.create<Optional<CourseBookDto>>()

    val selectedCourseBooks: Observable<CourseBookDto> =
        selectedCourseBooksSubject.hide().filterEmpty()

    val courseBooks = courseBookRepository.courseBooks
    private val tableMap = tableRepository.tableMap

    init {
        tableRepository.currentTable
            .filterEmpty()
            .firstElement()
            .subscribeBy {
                selectedCourseBooksSubject.onNext(CourseBookDto(it.semester, it.year).toOptional())
            }
    }

    val currentCourseBooksTable: Observable<List<TableDto>> =
        Observable.combineLatest(tableMap, selectedCourseBooks, { map, courseBook ->
            map.toList().map { it.second }.filter { table ->
                table.semester == courseBook.semester && table.year == courseBook.year
            }
        })

    fun selectCurrentCourseBook(courseBookDto: CourseBookDto) {
        courseBooks.firstElement()
            .subscribeBy {
                selectedCourseBooksSubject.onNext(courseBookDto.toOptional())
            }
    }

    fun changeCurrentTable(tableId: String) {
        tableRepository.refreshTable(tableId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = apiOnError)
    }

    fun createTable(tableName: String) {
        val currentCourseBook = selectedCourseBooksSubject.value.get() ?: return
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
