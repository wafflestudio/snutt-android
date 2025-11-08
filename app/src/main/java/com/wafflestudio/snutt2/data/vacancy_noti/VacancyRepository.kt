package com.wafflestudio.snutt2.data.vacancy_noti

import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import kotlinx.coroutines.flow.StateFlow

interface VacancyRepository {
    val firstVacancyVisit: StateFlow<Boolean>

    val firstVacancyAdd: StateFlow<Boolean>

    suspend fun getVacancyLectures(): List<LectureDto>

    suspend fun addVacancyLecture(lectureId: String)

    suspend fun removeVacancyLecture(lectureId: String)

    suspend fun setVacancyVisited()

    suspend fun setVacancyAdded()

    // 여기부터 리팩토링된 코드
    suspend fun getVacancyLecturesNew(): Result<List<SearchedLecture>>

    suspend fun removeVacancyLectureNew(lectureId: String): Result<Unit>

    suspend fun setVacancyVisitedNew(): Result<Unit>
}
