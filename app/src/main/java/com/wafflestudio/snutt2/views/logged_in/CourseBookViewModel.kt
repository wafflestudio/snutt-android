package com.wafflestudio.snutt2.views.logged_in

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.manager.TableManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@HiltViewModel
class CourseBookViewModel @Inject constructor(
    private val tableManager: TableManager,
    private val apiOnError: ApiOnError,
    private val storage: SNUTTStorage
) : ViewModel() {
    private val selectedCourseBookSubject = BehaviorSubject.create<CourseBookDto>()
    val selectedCourseBook = selectedCourseBookSubject.hide()

    val courseBooks = storage.courseBooks.asObservable()
    val tables = storage.tables.asObservable()

    val currentCourseBooksTable =
        Observable.combineLatest(tables, selectedCourseBookSubject, { list, courseBook ->
            list.filter { it.semester == courseBook.semester && it.year == courseBook.year }
        })

    fun refresh() {
        tableManager.getCoursebook()
            .subscribeBy(onError = apiOnError)
        tableManager.getTableList()
            .subscribeBy(onError = apiOnError)
    }

    fun selectCurrentCourseBook(year: Long, semester: Long) {
        selectedCourseBookSubject.onNext(CourseBookDto(semester, year))
    }
}
