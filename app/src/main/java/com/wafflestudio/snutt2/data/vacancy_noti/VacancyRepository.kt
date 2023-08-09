package com.wafflestudio.snutt2.data.vacancy_noti

import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import kotlinx.coroutines.flow.StateFlow

interface VacancyRepository {
    val firstVacancyVisit: StateFlow<Boolean>

    val vacancyBannerOpenTime: StateFlow<Long>

    suspend fun getVacancyLectures(): List<LectureDto>

    suspend fun addVacancyLecture(lectureId: String)

    suspend fun removeVacancyLecture(lectureId: String)

    suspend fun setVacancyVisited()

    suspend fun updateVacancyBannerOpenTime()
}
