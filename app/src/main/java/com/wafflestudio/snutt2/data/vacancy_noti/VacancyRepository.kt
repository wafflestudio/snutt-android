package com.wafflestudio.snutt2.data.vacancy_noti

import androidx.paging.PagingData
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import kotlinx.coroutines.flow.Flow

interface VacancyRepository {
    fun getVacancyLectureStream(

    ): Flow<List<LectureDto>>
}
