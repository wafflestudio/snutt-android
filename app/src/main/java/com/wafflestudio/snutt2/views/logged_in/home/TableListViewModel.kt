package com.wafflestudio.snutt2.views.logged_in.home

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.CourseBookRepository
import com.wafflestudio.snutt2.data.MyLectureRepository
import com.wafflestudio.snutt2.data.TableRepository
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.android.MessagingError
import com.wafflestudio.snutt2.lib.data.DataProvider
import com.wafflestudio.snutt2.lib.data.DataValue
import com.wafflestudio.snutt2.lib.data.SubjectDataValue
import com.wafflestudio.snutt2.lib.network.dto.PostCopyTableResults
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.lib.toOptional
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

@HiltViewModel
class TableListViewModel @Inject constructor(
    private val tableRepository: TableRepository,
    private val myLectureRepository: MyLectureRepository,
    courseBookRepository: CourseBookRepository,
    private val apiOnError: ApiOnError,
) : ViewModel() {

    private val _selectedCourseBooks = SubjectDataValue<Optional<CourseBookDto>>(Optional.empty())
    val selectedCourseBooks: DataValue<Optional<CourseBookDto>> = _selectedCourseBooks

    val courseBooks: DataProvider<List<CourseBookDto>> = courseBookRepository.courseBooks

    init {
        myLectureRepository.lastViewedTable.asObservable()
            .filterEmpty()
            .firstElement()
            .subscribeBy {
                _selectedCourseBooks.update(CourseBookDto(it.semester, it.year).toOptional())
            }
    }

    val selectedCourseBookTableList: Observable<List<SimpleTableDto>> =
        Observable.combineLatest(
            tableRepository.tableMap.asObservable(),
            selectedCourseBooks.asObservable().filterEmpty(),
            { map, courseBook ->
                map.toList().map { it.second }.filter { table ->
                    table.semester == courseBook.semester && table.year == courseBook.year
                }
            })

    fun setSelectedCourseBook(courseBookDto: CourseBookDto) {
        _selectedCourseBooks.update(courseBookDto.toOptional())
    }

    fun changeSelectedTable(tableId: String) {
        tableRepository.refreshTable(tableId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = apiOnError)
    }

    fun createTable(tableName: String): Completable {
        val currentCourseBook = _selectedCourseBooks.get().value ?: return Completable.error(
            MessagingError("currentCourseBook Not exists")
        )
        return tableRepository.createTable(
            currentCourseBook.year,
            currentCourseBook.semester,
            tableName
        )
            .ignoreElement()
    }

    fun copyTable(tableId: String): Single<PostCopyTableResults> {
        return tableRepository.copyTable(tableId)
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun checkTableDeletable(tableId: String): Boolean {
        return myLectureRepository.lastViewedTable.get().get()?.id == tableId
    }


    fun deleteTable(tableId: String): Single<List<SimpleTableDto>> {
        return tableRepository.deleteTable(tableId)
    }

    fun changeNameTable(tableId: String, name: String): Completable {
        return tableRepository.updateTableName(tableId, name)
            .ignoreElement()
    }
}
