package com.wafflestudio.snutt2.data.vacancy_noti

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.toDomainError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VacancyRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) : VacancyRepository {

    override val firstVacancyVisit = storage.firstVacancyVisit.asStateFlow()

    override val firstVacancyAdd = storage.firstVacancyAdd.asStateFlow()

    override suspend fun getVacancyLectures(): List<LectureDto> {
        return api._getVacancyLectures().lectures
    }

    override suspend fun addVacancyLecture(lectureId: String) {
        api._postVacancyLecture(lectureId)
    }

    override suspend fun addVacancyLectureNew(lectureId: String): Result<Unit> {
        try {
            val response = api._postVacancyLecture(lectureId)
            return Result.Success(response)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun removeVacancyLecture(lectureId: String) {
        api._deleteVacancyLecture(lectureId)
    }

    override suspend fun setVacancyVisited() {
        storage.firstVacancyVisit.update(false)
    }

    override suspend fun setVacancyAdded() {
        storage.firstVacancyAdd.update(false)
    }

    // 여기부터 리팩토링된 코드
    override suspend fun getVacancyLecturesNew(): Result<List<SearchedLecture>> {
        try {
            val result = api._getVacancyLectures()
            return Result.Success(result.lectures.map { it.toSearchedLecture() })
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun removeVacancyLectureNew(lectureId: String): Result<Unit> {
        try {
            api._deleteVacancyLecture(lectureId)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
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

    override suspend fun addVacancyLectureNewNew(lecture: Lecture): Result<Unit> {
        return addVacancyLectureNew(lecture.resolveApiId())
    }

    override suspend fun removeVacancyLectureNewNew(lecture: Lecture): Result<Unit> {
        return removeVacancyLectureNew(lecture.resolveApiId())
    }

    // NOTE: 서버 API에 보낼 강의 ID 필드를 결정한다.
    // 이 ID 선택 로직은 서버 스펙에 종속된 관심사이므로 data layer(repository)에서 처리한다.
    // 다른 repository에서도 같은 로직이 필요해지면 data layer 내 공유 유틸로 추출할 것.
    private fun Lecture.resolveApiId(): String = when (this) {
        is SyllabusLecture -> originalLectureId
        else -> id
    }

    override suspend fun setVacancyVisitedNew(): Result<Unit> {
        try {
            storage.firstVacancyVisit.update(false)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }
}
