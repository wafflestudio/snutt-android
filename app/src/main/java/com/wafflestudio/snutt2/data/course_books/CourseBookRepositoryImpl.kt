package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.core.data.toTempModel
import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSource
import com.wafflestudio.snutt2.data.toExternalModel
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseBookRepositoryImpl @Inject constructor(
    private val api: SNUTTNetworkDataSource,
) : CourseBookRepository {
    override suspend fun getCourseBook(): List<CourseBookDto> {
        return api._getCoursebook().map { it.toTempModel().toExternalModel() } // TODO : 변환 함수 사용 부분
    }
}
