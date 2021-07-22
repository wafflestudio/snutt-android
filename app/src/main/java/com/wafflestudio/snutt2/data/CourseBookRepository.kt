package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.GetCoursebookResults
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseBookRepository @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) {
    val courseBooks = storage.courseBooks.asObservable()

    fun fetchCourseBook(): Single<GetCoursebookResults> {
        return api.getCoursebook()
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                storage.courseBooks.setValue(it)
            }
    }
}
