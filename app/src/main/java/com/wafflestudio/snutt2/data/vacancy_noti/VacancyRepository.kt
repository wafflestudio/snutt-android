package com.wafflestudio.snutt2.data.vacancy_noti

import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto

interface VacancyRepository {
    suspend fun getVacancyLectures(): List<LectureDto>
    suspend fun addVacancyLecture(lectureId: String)

    suspend fun removeVacancyLecture(lectureId: String)
}
