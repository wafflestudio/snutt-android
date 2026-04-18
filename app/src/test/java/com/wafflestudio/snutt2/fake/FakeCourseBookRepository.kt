package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.data.coursebooks.CourseBookRepository
import com.wafflestudio.snutt2.domain.model.CourseBook
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCourseBookRepository : CourseBookRepository {

    override val courseBooks = MutableStateFlow<List<CourseBook>>(emptyList())

    var fetchCourseBooksResult: Result<Unit> = Result.Success(Unit)
    var fetchCourseBooksCalled = false
        private set

    override suspend fun fetchCourseBooks(): Result<Unit> {
        fetchCourseBooksCalled = true
        return fetchCourseBooksResult
    }
}
