package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseBookRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi
) : CourseBookRepository {
    override suspend fun getCourseBook(): List<CourseBookDto> {
        return api._getCoursebook()
    }
}
