package com.wafflestudio.snutt2.data.vacancy_noti

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.toDomainError
import java.util.*
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

    override suspend fun setVacancyVisitedNew(): Result<Unit> {
        try {
            storage.firstVacancyVisit.update(false)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }
}
