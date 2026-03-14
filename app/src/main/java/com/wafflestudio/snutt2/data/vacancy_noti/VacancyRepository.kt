package com.wafflestudio.snutt2.data.vacancy_noti

import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import kotlinx.coroutines.flow.StateFlow

interface VacancyRepository {
    val firstVacancyVisit: StateFlow<Boolean>

    val firstVacancyAdd: StateFlow<Boolean>

    suspend fun getVacancyLectures(): List<LectureDto>

    suspend fun addVacancyLecture(lectureId: String)

    suspend fun addVacancyLectureNew(lectureId: String): Result<Unit>

    suspend fun removeVacancyLecture(lectureId: String)

    suspend fun setVacancyVisited()

    suspend fun setVacancyAdded()

    // 여기부터 리팩토링된 코드
    suspend fun getVacancyLecturesNew(): Result<List<SearchedLecture>>

    suspend fun isVacancyRegistered(lecture: Lecture): Result<Boolean>

    suspend fun removeVacancyLectureNew(lectureId: String): Result<Unit>

    // Lecture 객체를 받아 repository 내부에서 API에 보낼 ID를 결정한다.
    // 기존 String 버전은 레거시 코드에서 사용 중이라 유지.
    suspend fun addVacancyLectureNewNew(lecture: Lecture): Result<Unit>

    suspend fun removeVacancyLectureNewNew(lecture: Lecture): Result<Unit>

    suspend fun setVacancyVisitedNew(): Result<Unit>
}
