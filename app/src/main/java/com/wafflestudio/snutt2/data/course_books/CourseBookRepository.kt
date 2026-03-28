package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.lib.network.Result
import kotlinx.coroutines.flow.StateFlow

interface CourseBookRepository {
    val courseBooks: StateFlow<List<CourseBook>>

    suspend fun getCourseBooks(): Result<List<CourseBook>>

    suspend fun fetchCourseBooks(): Result<Unit>
}
