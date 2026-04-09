package com.wafflestudio.snutt2.data.vacancy_noti

import com.wafflestudio.snutt2.storage.SNUTTStorage
import com.wafflestudio.snutt2.data.mapper.toSearchedLecture
import com.wafflestudio.snutt2.domain.model.Lecture
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.domain.model.SyllabusLecture
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.error.toDomainError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VacancyRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) : VacancyRepository {

    override val firstVacancyVisit = storage.firstVacancyVisit.asStateFlow()

    override val firstVacancyAdd = storage.firstVacancyAdd.asStateFlow()

    private val _vacancyLectures = MutableStateFlow<List<SearchedLecture>>(emptyList())
    override val vacancyLectures: StateFlow<List<SearchedLecture>> = _vacancyLectures.asStateFlow()

    private suspend fun refetch(): List<SearchedLecture> {
        val lectures = api._getVacancyLectures().lectures.map { it.toSearchedLecture() }
        _vacancyLectures.update { lectures }
        return lectures
    }

    override suspend fun fetchVacancyLectures(): Result<Unit> {
        return try {
            refetch()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun addVacancyLecture(lecture: Lecture): Result<Unit> {
        return try {
            api._postVacancyLecture(lecture.resolveApiId())
            refetch()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun removeVacancyLecture(lecture: Lecture): Result<Unit> {
        return try {
            api._deleteVacancyLecture(lecture.resolveApiId())
            refetch()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun isVacancyRegistered(lecture: Lecture): Result<Boolean> {
        try {
            val vacancyLectures = api._getVacancyLectures().lectures
            val targetId = lecture.resolveApiId()
            return Result.Success(vacancyLectures.any { it.id == targetId })
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun isLectureVacancyRegistered(lecture: Lecture): Result<Boolean> {
        return try {
            val list = refetch()
            val targetId = lecture.resolveApiId()
            Result.Success(list.any { it.id == targetId })
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun setVacancyVisited(): Result<Unit> {
        try {
            storage.firstVacancyVisit.update(false)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun setVacancyAdded() {
        storage.firstVacancyAdd.update(false)
    }

    // NOTE: 서버 API에 보낼 강의 ID 필드를 결정한다.
    // 이 ID 선택 로직은 서버 스펙에 종속된 관심사이므로 data layer(repository)에서 처리한다.
    // 다른 repository에서도 같은 로직이 필요해지면 data layer 내 공유 유틸로 추출할 것.
    private fun Lecture.resolveApiId(): String = when (this) {
        is SyllabusLecture -> originalLectureId
        else -> id
    }
}
