package com.wafflestudio.snutt2.data.semester_status

import com.wafflestudio.snutt2.storage.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.SemesterStatus
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.error.toDomainError
import com.wafflestudio.snutt2.data.mapper.toDomain
import com.wafflestudio.snutt2.storage.toOptional
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterStatusRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) : SemesterStatusRepository {

    private val _semesterStatus: MutableStateFlow<SemesterStatus?> =
        MutableStateFlow(storage.semesterStatus.get().value?.toDomain())
    override val semesterStatus: StateFlow<SemesterStatus?> = _semesterStatus.asStateFlow()

    override suspend fun fetchSemesterStatus(): Result<Unit> {
        try {
            val response = api._getSemesterStatus()
            storage.semesterStatus.update(response.toOptional())
            _semesterStatus.value = response.toDomain()
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }
}
