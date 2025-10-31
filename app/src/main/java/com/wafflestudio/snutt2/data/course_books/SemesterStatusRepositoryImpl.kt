package com.wafflestudio.snutt2.data.course_books

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.SemesterStatus
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.toOptional
import com.wafflestudio.snutt2.model.toDomainModel
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

    override suspend fun fetchSemesterStatus() {
        val response = api._getSemesterStatus()
        storage.semesterStatus.update(response.toOptional())
        _semesterStatus.value = response.toDomainModel()
    }
}
