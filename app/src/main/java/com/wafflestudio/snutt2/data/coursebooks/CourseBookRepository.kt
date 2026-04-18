package com.wafflestudio.snutt2.data.coursebooks

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.model.CourseBook
import kotlinx.coroutines.flow.StateFlow

interface CourseBookRepository {
    val courseBooks: StateFlow<List<CourseBook>>

    suspend fun fetchCourseBooks(): Result<Unit>
}
