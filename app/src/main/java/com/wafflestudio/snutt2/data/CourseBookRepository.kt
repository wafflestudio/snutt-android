package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.lib.network.ApiStatus
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.GetCoursebookResults
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.model.TagDto
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseBookRepository @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) {
    val courseBooks = storage.courseBooks.asObservable()

    fun loadCourseBook(): Single<GetCoursebookResults> {
        return api.getCoursebook()
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                storage.courseBooks.setValue(it)
            }
    }
}
