package com.wafflestudio.snutt2.data.semesterstatus

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.data.mapper.toDomain
import com.wafflestudio.snutt2.domain.model.SemesterStatus
import com.wafflestudio.snutt2.lib.map
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.error.toDomainError
import com.wafflestudio.snutt2.storage.SNUTTStorage
import com.wafflestudio.snutt2.storage.model.toDomainModel
import com.wafflestudio.snutt2.storage.model.toLocalEntity
import com.wafflestudio.snutt2.storage.model.toOptional
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterStatusRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
    externalScope: CoroutineScope,
) : SemesterStatusRepository {

    override val semesterStatus: StateFlow<SemesterStatus?> =
        storage.semesterStatus.asStateFlow().map(externalScope) { it.value?.toDomainModel() }

    override suspend fun fetchSemesterStatus(): Result<Unit> = try {
        val response = api._getSemesterStatus()
        val domain = response.toDomain()
        storage.semesterStatus.update(domain.toLocalEntity().toOptional())
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Fail(e.toDomainError())
    }
}
