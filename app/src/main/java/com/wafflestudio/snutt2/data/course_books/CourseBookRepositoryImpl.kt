package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSource
import com.wafflestudio.snutt2.core.qualifiers.App
import com.wafflestudio.snutt2.core.qualifiers.CoreNetwork
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.toExternalModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseBookRepositoryImpl @Inject constructor(
    @CoreNetwork private val api: SNUTTNetworkDataSource,
) : CourseBookRepository {
    override suspend fun getCourseBook(): List<CourseBookDto> {
        return api._getCoursebook().map { it.toExternalModel() }
    }
}
