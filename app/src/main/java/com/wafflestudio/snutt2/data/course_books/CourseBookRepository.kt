package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto

interface CourseBookRepository {
    suspend fun getCourseBook(): List<CourseBookDto>

    // 여기부터 리팩토링 코드
    suspend fun getCourseBookNew(): Result<List<CourseBook>>
}
