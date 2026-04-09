package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.data.Result
import kotlinx.coroutines.flow.StateFlow

interface CourseBookRepository {
    val courseBooks: StateFlow<List<CourseBook>>

    suspend fun fetchCourseBooks(): Result<Unit>
}
