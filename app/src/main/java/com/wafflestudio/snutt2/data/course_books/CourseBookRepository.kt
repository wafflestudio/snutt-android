package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import kotlinx.coroutines.flow.StateFlow

interface CourseBookRepository {
    suspend fun getCourseBook(): List<CourseBookDto>

    // 여기부터 리팩토링 코드
    suspend fun getCourseBookNew(): Result<List<CourseBook>>

    // 레포지토리 정리할 때 위치 수정할 것
    val courseBooks: StateFlow<List<CourseBook>>

    suspend fun fetchCourseBooks(): Result<Unit>
}
