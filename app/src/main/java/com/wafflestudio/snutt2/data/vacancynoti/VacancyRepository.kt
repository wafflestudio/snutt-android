package com.wafflestudio.snutt2.data.vacancynoti

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.model.Lecture
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import kotlinx.coroutines.flow.StateFlow

interface VacancyRepository {
    val firstVacancyVisit: StateFlow<Boolean>

    val firstVacancyAdd: StateFlow<Boolean>

    val vacancyLectures: StateFlow<List<SearchedLecture>>

    suspend fun fetchVacancyLectures(): Result<Unit>

    suspend fun addVacancyLecture(lecture: Lecture): Result<Unit>

    suspend fun removeVacancyLecture(lecture: Lecture): Result<Unit>

    // FIXME: 얘는 뭐고 밑에 isLectureVacancyRegistered 는 뭐지
    suspend fun isVacancyRegistered(lecture: Lecture): Result<Boolean>

    suspend fun isLectureVacancyRegistered(lecture: Lecture): Result<Boolean>

    suspend fun setVacancyVisited(): Result<Unit>

    suspend fun setVacancyAdded()
}
