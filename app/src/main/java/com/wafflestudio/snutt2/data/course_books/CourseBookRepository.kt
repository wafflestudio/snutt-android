package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto

interface CourseBookRepository {
    suspend fun getCourseBook(): List<CourseBookDto>
}
