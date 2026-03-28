package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.toDomainModel
import com.wafflestudio.snutt2.lib.network.toDomainError
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseBookRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
) : CourseBookRepository {

    override val courseBooks: MutableStateFlow<List<CourseBook>> = MutableStateFlow(emptyList())

    override suspend fun getCourseBooks(): Result<List<CourseBook>> {
        try {
            val result = api._getCoursebook()
            return Result.Success(result.map { it.toDomainModel() })
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun fetchCourseBooks(): Result<Unit> {
        try {
            courseBooks.value = api._getCoursebook().map { it.toDomainModel() }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }
}
