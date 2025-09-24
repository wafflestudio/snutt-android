package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.SemesterStatus
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.GetSemesterStatusResults
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterStatusRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) : SemesterStatusRepository {
    override val semesterStatus: StateFlow<SemesterStatus> = storage.semesterStatus.asStateFlow()
    override suspend fun getSemesterStatus() {
//        val response = api._semesterStatus()
        val response = GetSemesterStatusResults(
            current = CourseBook(year = 2025L, semester = 2),
            next = CourseBook(year = 2025L, semester = 3),
        )
        storage.semesterStatus.update(response)
    }
}
