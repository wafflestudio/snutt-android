package com.wafflestudio.snutt2.data.vacancy_noti

import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto

interface VacancyRepository {
    fun getVacancyLectures(): List<LectureDto>
}
