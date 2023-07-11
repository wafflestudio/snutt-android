package com.wafflestudio.snutt2.data.vacancy_noti

import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import kotlinx.coroutines.flow.Flow

interface VacancyRepository {
    fun getVacancyLectures(): List<LectureDto>
}
