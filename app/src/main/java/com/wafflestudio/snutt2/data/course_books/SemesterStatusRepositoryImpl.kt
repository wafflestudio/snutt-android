package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.model.SemesterStatus
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterStatusRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) : SemesterStatusRepository {
    override val semesterStatus: StateFlow<SemesterStatus> = storage.semesterStatus.asStateFlow()
    override suspend fun fetchSemesterStatus() {
        val response = api._getSemesterStatus()
        storage.semesterStatus.update(response)
    }
}
