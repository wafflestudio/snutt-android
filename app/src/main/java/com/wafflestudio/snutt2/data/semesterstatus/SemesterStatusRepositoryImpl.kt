package com.wafflestudio.snutt2.data.semesterstatus

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.data.mapper.toDomain
import com.wafflestudio.snutt2.domain.model.SemesterStatus
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.error.toDomainError
import com.wafflestudio.snutt2.storage.SNUTTStorage
import com.wafflestudio.snutt2.storage.model.toDomainModel
import com.wafflestudio.snutt2.storage.model.toLocalEntity
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
        MutableStateFlow(storage.semesterStatus.get().value?.toDomainModel())
    override val semesterStatus: StateFlow<SemesterStatus?> = _semesterStatus.asStateFlow()

    override suspend fun fetchSemesterStatus(): Result<Unit> {
        try {
            val response = api._getSemesterStatus()
            val domain = response.toDomain()
            storage.semesterStatus.update(domain.toLocalEntity().toOptional())
            _semesterStatus.value = domain
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }
}
