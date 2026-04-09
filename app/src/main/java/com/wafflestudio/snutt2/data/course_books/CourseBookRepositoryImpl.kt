package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.data.mapper.toDomain
import com.wafflestudio.snutt2.network.error.toDomainError
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseBookRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
) : CourseBookRepository {

    override val courseBooks: MutableStateFlow<List<CourseBook>> = MutableStateFlow(emptyList())

    override suspend fun fetchCourseBooks(): Result<Unit> {
        try {
            courseBooks.value = api._getCoursebook().map { it.toDomain() }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }
}
