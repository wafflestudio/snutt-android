package com.wafflestudio.snutt2.data.vacancy_noti

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VacancyRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) : VacancyRepository {

    override val firstVacancyVisit = storage.firstVacancyVisit.asStateFlow()

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
}
