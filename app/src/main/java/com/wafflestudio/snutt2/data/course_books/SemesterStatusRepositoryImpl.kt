package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.map
import com.wafflestudio.snutt2.model.SemesterStatus
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.toOptional
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterStatusRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) : SemesterStatusRepository {
    override val semesterStatus: Flow<SemesterStatus> = storage.semesterStatus.asStateFlow().map { it.get() ?: SemesterStatus.Default }
    override suspend fun fetchSemesterStatus() {
        val response = api._getSemesterStatus()
        storage.semesterStatus.update(response.toOptional())
    }
}
